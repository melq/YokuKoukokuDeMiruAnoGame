package io.github.melq.yokukoukokudemiruanogame

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.melq.yokukoukokudemiruanogame.ui.theme.YokuKoukokuDeMiruAnoGameTheme

class MainActivity : Activity() {

    // GameView のインスタンス（ゲームループや描画処理が実装済みのクラス）
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 例：DisplayMetricsを使って画面幅を取得
        val screenWidth = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels.toFloat()
        // GameView に screenWidth を渡す（GameView 内で Player インスタンス生成時に利用）
        gameView = GameView(this, screenWidth, screenHeight)
        // GameView をこのアクティビティのコンテンツビューに設定する
        setContentView(gameView)
    }

    override fun onPause() {
        super.onPause()
        // 画面が非表示になる際、ゲームループを一時停止する
        gameView.pause()  // GameView 内で running フラグ等を制御している前提
    }

    override fun onResume() {
        super.onResume()
        // 画面再表示時、ゲームループを再開する
        gameView.resume() // GameView 内でスレッドを再スタートする前提
    }
}