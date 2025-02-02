package io.github.melq.yokukoukokudemiruanogame.objects

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF

class Player(context: Context, private val screenWidth: Float, private val screenHeight: Float) {
    var x = 0f
    var y = 0f
    var width = 100f
    var height = 100f
    val rect: RectF
        get() = RectF(x, y, x + width, y + height)

    // パワーアップ効果パラメータ
    var damageMultiplier = 1.0
    var fireIntervalMultiplier = 1.0
    var allyCount = 1

    init {
        // 初期位置：画面下部中央
        x = (screenWidth - width) / 2
        y = screenHeight - height - 100 // ※実際は画面高さ等に合わせて調整
        // 必要に応じて、bitmap のリサイズなども行う
    }

    fun reset() {
        // 初期位置：画面下部中央
        x = (screenWidth - width) / 2
        y = screenHeight - height - 100 // ※実際は画面高さ等に合わせて調整
        damageMultiplier = 1.0
        fireIntervalMultiplier = 1.0
        allyCount = 1
    }

    // update() では、移動した x 座標が画面外に出ないよう補正する例
    fun update(deltaTime: Float) {
        // ※ここでは移動処理は onTouchEvent で move() しているので、
        //    update() では単に座標が画面内にあるか補正するだけです
        if (x < 0f) x = 0f
        if (x + width > screenWidth) x = screenWidth - width
    }

    fun move(dx: Float) {
        x += dx
    }

    fun fire(): Bullet {
        // 弾はプレイヤーの中央上部から発射。火力は damageMultiplier により調整可能
        return Bullet(x + width / 2 - 10, y - 20, 20f, 40f, -800f, damageMultiplier)
    }

    fun applyPowerUp(powerUp: PowerUp) {
        // パワーアップ効果をパラメータに加算（比率は powerUp.effectRatio などで調整可能）
        when (powerUp.type) {
            PowerUp.Type.DAMAGE -> damageMultiplier += powerUp.effectRatio
            PowerUp.Type.FIRE_RATE -> fireIntervalMultiplier *= (1 - powerUp.effectRatio)
            PowerUp.Type.ALLY -> allyCount += powerUp.extraCount
        }
    }

    fun draw(canvas: Canvas) {
        val paint = Paint().apply { color = Color.GREEN }
        canvas.drawRect(rect, paint)
    }

    public fun getStatus(): String {
        return "Damage: ${kotlin.math.round(damageMultiplier * 100)/100},\nFireRate: ${kotlin.math.round(fireIntervalMultiplier * 100)/100},\nAllies: $allyCount"
    }
}