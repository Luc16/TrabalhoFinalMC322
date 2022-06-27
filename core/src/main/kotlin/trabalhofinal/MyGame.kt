package trabalhofinal

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import trabalhofinal.screens.*

const val WIDTH = 1000f
const val HEIGHT = 1000f

class MyGame: KtxGame<CustomScreen>() {
    val renderer: ShapeRenderer by lazy { ShapeRenderer() }
    val font: BitmapFont by lazy { BitmapFont() }
    val batch: Batch by lazy { SpriteBatch() }
    val gameViewport = FitViewport(WIDTH, HEIGHT)

    override fun create() {
        gameViewport.camera.translate(WIDTH/2, HEIGHT/2,0f)
        font.data.setScale(3f)
        addScreen(GameScreen(this))
        addScreen(MenuScreen(this))
        addScreen(WinScreen(this))
        addScreen(LoseScreen(this))
        addScreen(InstructionScreen(this))
        setScreen<MenuScreen>()
    }

    override fun dispose() {
        super.dispose()
        renderer.dispose()
        batch.dispose()
        font.dispose()
    }
}