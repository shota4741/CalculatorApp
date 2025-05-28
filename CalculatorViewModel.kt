package com.example.moderncalculator

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.ExpressionBuilder
import kotlin.math.*

data class CalculationHistory(
    val expression: String,
    val result: String
)

class CalculatorViewModel : ViewModel() {
    var expression by mutableStateOf("")
        private set
    
    var result by mutableStateOf("0")
        private set
    
    var isDarkTheme by mutableStateOf(true)
        private set
    
    var isScientificMode by mutableStateOf(false)
        private set
    
    private val _history = mutableStateListOf<CalculationHistory>()
    val history: List<CalculationHistory> = _history
    
    fun toggleTheme() {
        isDarkTheme = !isDarkTheme
    }
    
    fun toggleScientificMode() {
        isScientificMode = !isScientificMode
    }
    
    fun updateExpression(expr: String) {
        expression = expr
        calculateResult()
    }
    
    fun onButtonClick(button: String) {
        when (button) {
            "C" -> clear()
            "⌫" -> backspace()
            "=" -> evaluate()
            "±" -> toggleSign()
            "%" -> percentage()
            "×" -> appendOperator("*")
            "÷" -> appendOperator("/")
            "+", "-" -> appendOperator(button)
            "." -> appendDecimal()
            else -> appendNumber(button)
        }
    }
    
    fun onScientificButtonClick(button: String) {
        when (button) {
            "sin" -> appendFunction("sin(")
            "cos" -> appendFunction("cos(")
            "tan" -> appendFunction("tan(")
            "ln" -> appendFunction("ln(")
            "log" -> appendFunction("log10(")
            "√" -> appendFunction("sqrt(")
            "x²" -> square()
            "x^y" -> appendOperator("^")
            "π" -> appendConstant(PI.toString())
            "e" -> appendConstant(E.toString())
            "(" -> appendBracket("(")
            ")" -> appendBracket(")")
        }
    }
    
    private fun clear() {
        expression = ""
        result = "0"
    }
    
    private fun backspace() {
        if (expression.isNotEmpty()) {
            expression = expression.dropLast(1)
            calculateResult()
        }
    }
    
    private fun appendNumber(number: String) {
        expression += number
        calculateResult()
    }
    
    private fun appendOperator(operator: String) {
        if (expression.isNotEmpty() && !expression.last().isOperator()) {
            expression += operator
        }
    }
    
    private fun appendDecimal() {
        val lastNumber = expression.split(Regex("[+\\-*/^]")).lastOrNull() ?: ""
        if (!lastNumber.contains(".")) {
            expression += if (expression.isEmpty() || expression.last().isOperator()) "0." else "."
            calculateResult()
        }
    }
    
    private fun appendFunction(function: String) {
        expression += function
    }
    
    private fun appendConstant(constant: String) {
        expression += constant
        calculateResult()
    }
    
    private fun appendBracket(bracket: String) {
        expression += bracket
        calculateResult()
    }
    
    private fun toggleSign() {
        if (expression.isNotEmpty()) {
            val parts = expression.split(Regex("(?=[+\\-*/^])|(?<=[+\\-*/^])"))
            if (parts.isNotEmpty()) {
                val lastPart = parts.last()
                if (lastPart.toDoubleOrNull() != null) {
                    val newLastPart = if (lastPart.startsWith("-")) {
                        lastPart.substring(1)
                    } else {
                        "-$lastPart"
                    }
                    expression = parts.dropLast(1).joinToString("") + newLastPart
                    calculateResult()
                }
            }
        }
    }
    
    private fun percentage() {
        try {
            val value = expression.toDoubleOrNull()
            if (value != null) {
                expression = (value / 100).toString()
                calculateResult()
            }
        } catch (e: Exception) {
            // Ignore
        }
    }
    
    private fun square() {
        if (expression.isNotEmpty() && expression.toDoubleOrNull() != null) {
            expression = "($expression)^2"
            calculateResult()
        }
    }
    
    private fun evaluate() {
        if (expression.isNotEmpty() && result != "Error") {
            _history.add(CalculationHistory(expression, result))
            expression = result
            calculateResult()
        }
    }
    
    private fun calculateResult() {
        if (expression.isEmpty()) {
            result = "0"
            return
        }
        
        try {
            val processedExpression = expression
                .replace("×", "*")
                .replace("÷", "/")
                .replace("π", PI.toString())
                .replace("e", E.toString())
            
            val expressionBuilder = ExpressionBuilder(processedExpression)
                .build()
            
            val calculation = expressionBuilder.evaluate()
            result = formatResult(calculation)
        } catch (e: Exception) {
            result = if (expression.isNotEmpty()) "0" else "0"
        }
    }
    
    private fun formatResult(value: Double): String {
        return if (value % 1 == 0.0) {
            value.toLong().toString()
        } else {
            String.format("%.6f", value).trimEnd('0').trimEnd('.')
        }
    }
    
    fun clearHistory() {
        _history.clear()
    }
    
    private fun Char.isOperator(): Boolean = this in "+-*/^"
}
