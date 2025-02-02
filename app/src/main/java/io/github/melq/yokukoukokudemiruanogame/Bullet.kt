package io.github.melq.yokukoukokudemiruanogame

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class Bullet(
    var x: Float,
    var y: Float,
    val width: Float,
    val height: Float,
    private val speed: Float, // 負の値なら上方向
    val damageMultiplier: Double
) {
    val rect: RectF
        get() = RectF(x, y, x + width, y + height)

    fun update(deltaTime: Float) {
        // 簡単な垂直移動
        y += speed * deltaTime
    }

    fun draw(canvas: Canvas) {
        val paint = Paint().apply { color = Color.YELLOW }
        canvas.drawRect(rect, paint)
    }
}