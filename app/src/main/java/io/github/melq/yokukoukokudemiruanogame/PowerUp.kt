package io.github.melq.yokukoukokudemiruanogame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class PowerUp(context: Context, var x: Float, var y: Float) {
    var width = 60f
    var height = 60f
    var speed = 100f
    // パワーアップの種類と効果（ここでは例としてダメージ増・発射間隔短縮・仲間増加）
    enum class Type { DAMAGE, FIRE_RATE, ALLY }
    val type = when ((0..2).random()) {
        0 -> Type.DAMAGE
        1 -> Type.FIRE_RATE
        else -> Type.ALLY
    }
    // 効果の強さ（パラメータで調整可能）
    val effectRatio = 0.2  // 例：20%アップ
    val extraCount = 1     // 仲間追加の場合

    val rect: RectF
        get() = RectF(x, y, x + width, y + height)

    fun update(deltaTime: Float) {
        y += speed * deltaTime
    }

    fun draw(canvas: Canvas) {
        val paint = Paint().apply { color = Color.BLUE }
        canvas.drawRect(rect, paint)
    }
}