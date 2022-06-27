package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Texture
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.MyGame
import trabalhofinal.WIDTH

abstract class ImageScreen(game: MyGame):CustomScreen(game), InputProcessor {
    abstract val texture: Texture


    override fun show() {
        Gdx.input.inputProcessor = this
    }

    override fun render(delta: Float) {
        Gdx.input.isCursorCatched = false
        batch.use(viewport.camera.combined) {
            it.draw(texture, 0f, 0f, WIDTH, HEIGHT)
        }
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        game.setScreen<MenuScreen>()
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = true

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = true

    override fun keyDown(keycode: Int): Boolean {
        game.setScreen<MenuScreen>()
        return true
    }
    override fun keyUp(keycode: Int): Boolean = true
    override fun keyTyped(character: Char): Boolean = true
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = true
    override fun scrolled(amountX: Float, amountY: Float): Boolean = true
}