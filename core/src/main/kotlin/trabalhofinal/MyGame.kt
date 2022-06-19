package trabalhofinal

import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.app.KtxGame
import trabalhofinal.screens.GridScreen
import trabalhofinal.screens.*

const val WIDTH = 800f
const val HEIGHT = 800f

class MyGame: KtxGame<CustomScreen>() {
    val renderer: ShapeRenderer by lazy { ShapeRenderer() }
    val font: BitmapFont by lazy { BitmapFont() }
    val batch: Batch by lazy { SpriteBatch() }
    val gameViewport = FitViewport(WIDTH, HEIGHT)
    val multiplexer: InputMultiplexer by lazy { InputMultiplexer() }

    override fun create() {
        gameViewport.camera.translate(WIDTH/2, HEIGHT/2,0f)
        font.data.scale(2f)
        addScreen(RayCastingTestScreen(this))
        addScreen(MeshTestScreen(this))
        addScreen(GridScreen(this))
        addScreen(MovementTestScreen(this))
        addScreen(MenuScreen(this))
        setScreen<MenuScreen>()
    }

    override fun dispose() {
        super.dispose()
        renderer.dispose()
        batch.dispose()
        font.dispose()
    }
}