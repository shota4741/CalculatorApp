package com.example.moderncalculator

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moderncalculator.ui.theme.ModernCalculatorTheme
import kotlinx.coroutines.launch
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ModernCalculatorTheme {
                CalculatorApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorApp() {
    val viewModel: CalculatorViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    
    ModernCalculatorTheme(darkTheme = viewModel.isDarkTheme) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    drawerContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    CalculatorHistory(
                        history = viewModel.history,
                        onHistoryItemClick = { item ->
                            viewModel.updateExpression(item.expression)
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        onClearHistory = { viewModel.clearHistory() }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { 
                            Text(
                                "Modern Calculator",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { viewModel.toggleTheme() }
                            ) {
                                Icon(
                                    if (viewModel.isDarkTheme) Icons.Default.LightMode 
                                    else Icons.Default.DarkMode,
                                    contentDescription = "Toggle Theme"
                                )
                            }
                            IconButton(
                                onClick = { viewModel.toggleScientificMode() }
                            ) {
                                Icon(
                                    Icons.Default.Functions,
                                    contentDescription = "Scientific Mode",
                                    tint = if (viewModel.isScientificMode) 
                                        MaterialTheme.colorScheme.primary 
                                    else 
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                }
            ) { paddingValues ->
                CalculatorScreen(
                    modifier = Modifier.padding(paddingValues),
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel
) {
    val view = LocalView.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Display
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            border = BorderStroke(
                1.dp,
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.End
            ) {
                AnimatedContent(
                    targetState = viewModel.expression,
                    transitionSpec = {
                        slideInVertically { it } + fadeIn() togetherWith
                        slideOutVertically { -it } + fadeOut()
                    },
                    label = "expression"
                ) { expression ->
                    Text(
                        text = expression.ifEmpty { "0" },
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                AnimatedContent(
                    targetState = viewModel.result,
                    transitionSpec = {
                        (slideInVertically { it } + fadeIn() + scaleIn()) togetherWith
                        (slideOutVertically { -it } + fadeOut() + scaleOut())
                    },
                    label = "result"
                ) { result ->
                    Text(
                        text = result,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Scientific Functions (if enabled)
        AnimatedVisibility(
            visible = viewModel.isScientificMode,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            ScientificButtons(
                onButtonClick = { button ->
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    viewModel.onScientificButtonClick(button)
                }
            )
        }
        
        // Main Calculator Buttons
        CalculatorButtons(
            modifier = Modifier.weight(0.7f),
            onButtonClick = { button ->
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                viewModel.onButtonClick(button)
                
                if (button == "=") {
                    // Stronger vibration for calculation completion
                    val vibrator = view.context.getSystemService(Vibrator::class.java)
                    if (vibrator?.hasVibrator() == true) {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun CalculatorButtons(
    modifier: Modifier = Modifier,
    onButtonClick: (String) -> Unit
) {
    val buttons = listOf(
        listOf("C", "⌫", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("±", "0", ".", "=")
    )
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        buttons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { button ->
                    CalculatorButton(
                        text = button,
                        modifier = Modifier.weight(if (button == "0") 2f else 1f),
                        onClick = { onButtonClick(button) }
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isOperator = text in listOf("+", "-", "×", "÷", "=")
    val isSpecial = text in listOf("C", "⌫", "%", "±")
    
    val backgroundColor = when {
        text == "=" -> MaterialTheme.colorScheme.primary
        isOperator -> MaterialTheme.colorScheme.secondary
        isSpecial -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Surface(
        modifier = modifier
            .aspectRatio(if (text == "0") 2f else 1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        shadowElevation = 8.dp,
        onClick = onClick,
        interactionSource = interactionSource
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    if (text == "=") {
                        drawGradientGlow()
                    }
                }
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium,
                color = if (text == "=" || isOperator) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ScientificButtons(
    onButtonClick: (String) -> Unit
) {
    val scientificButtons = listOf(
        listOf("sin", "cos", "tan", "π"),
        listOf("ln", "log", "√", "x²"),
        listOf("e", "(", ")", "x^y")
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        scientificButtons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { button ->
                    ScientificButton(
                        text = button,
                        modifier = Modifier.weight(1f),
                        onClick = { onButtonClick(button) }
                    )
                }
            }
        }
    }
}

@Composable
fun ScientificButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CalculatorHistory(
    history: List<CalculationHistory>,
    onHistoryItemClick: (CalculationHistory) -> Unit,
    onClearHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "History",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = onClearHistory) {
                Icon(Icons.Default.Delete, contentDescription = "Clear History")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history.reversed()) { item ->
                HistoryItem(
                    item = item,
                    onClick = { onHistoryItemClick(item) }
                )
            }
        }
    }
}

@Composable
fun HistoryItem(
    item: CalculationHistory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = item.expression,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "= ${item.result}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun DrawScope.drawGradientGlow() {
    val gradient = Brush.radialGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.3f),
            Color.Transparent
        ),
        center = Offset(size.width / 2, size.height / 2),
        radius = size.minDimension / 2
    )
    drawCircle(gradient)
}
