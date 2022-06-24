package trabalhofinal.screens


import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxScreen
import trabalhofinal.MyGame

abstract class CustomScreen(
    val game: MyGame,
    val batch: Batch = game.batch,
    val renderer: ShapeRenderer = game.renderer,
    val font: BitmapFont = game.font,
    val viewport: FitViewport = game.gameViewport,
) : KtxScreen {
    override fun resize(width: Int, height: Int) {
        viewport.update(width, height)
    }

    fun unprojectedMouse(screenX: Int, screenY: Int): Vector2 {
        val mouse = Vector2(screenX.toFloat(), screenY.toFloat())
        viewport.unproject(mouse)
        return mouse
    }
}