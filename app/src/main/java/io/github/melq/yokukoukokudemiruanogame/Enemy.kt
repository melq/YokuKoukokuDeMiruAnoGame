package io.github.melq.yokukoukokudemiruanogame

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class Enemy(context: Context, var x: Float, var y: Float) {
    var width = 80f
    var height = 80f
    var speed = 150f  // 下方向へ移動（ピクセル/秒）

    val rect: RectF
        get() = RectF(x, y, x + width, y + height)

    fun update(deltaTime: Float) {
        y += speed * deltaTime
    }

    fun draw(canvas: Canvas) {
        val paint = Paint().apply { color = Color.RED }
        canvas.drawRect(rect, paint)
    }
}