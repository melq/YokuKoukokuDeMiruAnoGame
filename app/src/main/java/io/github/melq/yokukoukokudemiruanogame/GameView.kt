package io.github.melq.yokukoukokudemiruanogame

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GameView(context: Context, screenWidth: Float, screenHeight: Float) : SurfaceView(context), SurfaceHolder.Callback, Runnable {
    @Volatile
    private var running = false
    private var gameThread: Thread? = null

    // ゲーム内オブジェクト（プレイヤー、敵、パワーアップ、弾など）
    private val player = Player(context, screenWidth, screenHeight)
    private val enemies = mutableListOf<Enemy>()
    private val powerUps = mutableListOf<PowerUp>()
    private val bullets = mutableListOf<Bullet>()

    // 自動発射用タイマー（ミリ秒）
    private var lastBulletTime = System.currentTimeMillis()
    private val bulletInterval = 500L // 0.5 second

    // 敵出現間隔（例：2000ms）
    private var lastEnemySpawn = System.currentTimeMillis()
    private val enemySpawnInterval = 2000L

    // パワーアップ出現間隔（例：5000ms）
    private var lastPowerUpSpawn = System.currentTimeMillis()
    private val powerUpSpawnInterval = 5000L

    // 描画用 Paint
    private val paint = Paint()

    // タッチ操作用（プレイヤー左右移動）
    private var touchStartX = 0f

    init {
        holder.addCallback(this)
    }

    override fun run() {
        var previousTime = System.currentTimeMillis()
        while (running) {
            val currentTime = System.currentTimeMillis()
            val deltaTime = (currentTime - previousTime) / 1000f  // 秒単位
            previousTime = currentTime

            update(deltaTime)
            drawGame()
        }
    }

    private fun update(deltaTime: Float) {
        // プレイヤー更新（移動状態は onTouchEvent で変更済み）
        player.update(deltaTime)

        // 一定間隔で自動発射
        if (System.currentTimeMillis() - lastBulletTime >= bulletInterval) {
            // プレイヤーから上方向へ弾を発射
            bullets.add(player.fire())
            lastBulletTime = System.currentTimeMillis()
        }

        // 弾の更新（上方向に移動）
        bullets.forEach { it.update(deltaTime) }
        // 不要な弾（画面外）は削除
        bullets.removeAll { it.y + it.height < 0 }

        // 敵の更新（下方向へ移動）
        enemies.forEach { it.update(deltaTime) }
        enemies.removeAll { it.y > height }

        // パワーアップアイテムの更新（下方向へ移動）
        powerUps.forEach { it.update(deltaTime) }
        powerUps.removeAll { it.y > height }

        // 敵およびパワーアップの出現
        if (System.currentTimeMillis() - lastEnemySpawn >= enemySpawnInterval) {
            enemies.add(Enemy(context, (Math.random() * (width - 100)).toFloat(), 0f))
            lastEnemySpawn = System.currentTimeMillis()
        }
        if (System.currentTimeMillis() - lastPowerUpSpawn >= powerUpSpawnInterval) {
            powerUps.add(PowerUp(context, (Math.random() * (width - 100)).toFloat(), 0f))
            lastPowerUpSpawn = System.currentTimeMillis()
        }

        // 衝突判定
        checkCollisions()
    }

    private fun checkCollisions() {
        // 敵とプレイヤーの弾との衝突
        val bulletIterator = bullets.iterator()
        while (bulletIterator.hasNext()) {
            val bullet = bulletIterator.next()
            val enemyIterator = enemies.iterator()
            while (enemyIterator.hasNext()) {
                val enemy = enemyIterator.next()
                if (RectF.intersects(bullet.rect, enemy.rect)) {
                    // 衝突時：敵を消去、スコア加算など
                    enemyIterator.remove()
                    bulletIterator.remove()
                    // ここで効果音再生なども可能
                    break
                }
            }
        }
        // プレイヤーとパワーアップアイテムの衝突
        val powerUpIterator = powerUps.iterator()
        while (powerUpIterator.hasNext()) {
            val powerUp = powerUpIterator.next()
            if (RectF.intersects(powerUp.rect, player.rect)) {
                // 衝突時：パワーアップの効果をプレイヤーに適用
                player.applyPowerUp(powerUp)
                powerUpIterator.remove()
            }
        }
    }

    private fun drawGame() {
        val canvas = holder.lockCanvas()
        if (canvas != null) {
            // 画面クリア（黒で塗りつぶし）
            canvas.drawColor(Color.BLACK)
            // プレイヤー描画
            player.draw(canvas)
            // 敵描画
            enemies.forEach { it.draw(canvas) }
            // パワーアップ描画
            powerUps.forEach { it.draw(canvas) }
            // 弾描画
            bullets.forEach { it.draw(canvas) }
            holder.unlockCanvasAndPost(canvas)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        running = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) { }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    // ゲームループを一時停止するメソッド
    fun pause() {
        running = false
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    // ゲームループを再開するメソッド
    fun resume() {
        running = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    // タッチイベント：左右スワイプでプレイヤー移動
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - touchStartX
                player.move(dx)
                touchStartX = event.x
            }
        }
        return true
    }
}