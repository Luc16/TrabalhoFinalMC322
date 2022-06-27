package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.MyGame

class LoseScreen(game: MyGame):ImageScreen(game), InputProcessor {
    override val texture = Texture(Gdx.files.local("assets/textures/loseScreen.png"))
}