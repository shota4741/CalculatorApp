# Modern Calculator App

Android Studio 2024.3.2対応のモダンな電卓アプリです。

## 主な機能

### 基本機能
- ✨ **近代的なUI/UX**: ゲーム系サイトにインスパイアされたネオン調のデザイン
- 🌓 **テーマ切り替え**: ダーク/ライトモードの切り替え
- 📳 **振動フィードバック**: ボタンタップと計算完了時の触覚フィードバック
- 📱 **レスポンシブデザイン**: スムーズなアニメーションとエフェクト

### 計算機能
- 🔢 **基本計算**: 加算、減算、乗算、除算
- 🧮 **高度な機能**: 関数電卓モード（sin, cos, tan, log, √など）
- 📊 **計算履歴**: スワイプで過去の計算を確認・再利用
- ✅ **リアルタイム計算**: 入力中に結果をプレビュー

## セットアップ手順

### 1. プロジェクトの作成
1. Android Studio 2024.3.2を起動
2. 「New Project」→「Empty Activity」を選択
3. プロジェクト名: `ModernCalculator`
4. Package name: `com.example.moderncalculator`
5. Language: Kotlin
6. Minimum SDK: API 26 (Android 8.0)
7. Build configuration language: Kotlin DSL

### 2. ファイルの配置

#### メインコード
- `MainActivity.kt` → `app/src/main/java/com/example/moderncalculator/`
- `CalculatorViewModel.kt` → `app/src/main/java/com/example/moderncalculator/`

#### テーマファイル
- `Theme.kt` → `app/src/main/java/com/example/moderncalculator/ui/theme/`
- `Type.kt` → `app/src/main/java/com/example/moderncalculator/ui/theme/`

#### 設定ファイル
- `build.gradle.kts` → `app/` (既存ファイルを置き換え)
- `AndroidManifest.xml` → `app/src/main/` (既存ファイルを置き換え)

### 3. 依存関係の同期
1. `build.gradle.kts`を更新後、「Sync Now」をクリック
2. 必要なライブラリがダウンロードされるまで待機

### 4. 実行
1. エミュレータまたは実機を接続
2. 「Run」ボタンをクリック

## 使い方

### 基本操作
- **数字ボタン**: 数値を入力
- **演算子ボタン**: +, -, ×, ÷で計算
- **=ボタン**: 計算を実行（振動フィードバック付き）
- **Cボタン**: 全てクリア
- **⌫ボタン**: 一文字削除

### 特殊機能
- **メニューボタン**: 計算履歴を表示
- **太陽/月アイコン**: テーマ切り替え
- **関数アイコン**: 科学計算モードの切り替え

### ジェスチャー
- **左スワイプ**: 計算履歴を表示
- **履歴タップ**: 過去の計算を再利用

## カスタマイズ

### カラーテーマの変更
`Theme.kt`ファイルの色定義を編集:
```kotlin
primary = Color(0xFF00E5FF),      // メインカラー
secondary = Color(0xFFFF00E5),    // セカンダリカラー
tertiary = Color(0xFFFFE500),     // アクセントカラー
```

### 振動の強さ調整
`MainActivity.kt`の振動設定を変更:
```kotlin
VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
// 50 = 振動時間（ミリ秒）
// DEFAULT_AMPLITUDE = 振動の強さ
```

## トラブルシューティング

### ビルドエラー
- Gradle Syncを実行
- Build → Clean Project → Rebuild Project

### 振動が動作しない
- エミュレータでは振動機能が動作しません
- 実機でテストしてください

### テーマが切り替わらない
- Android 12以降のDynamic Colorが有効な場合、システムテーマが優先されます
- `Theme.kt`の`dynamicColor`を`false`に設定してください

## ライセンス
このプロジェクトはサンプルコードです。自由に使用・改変できます。
