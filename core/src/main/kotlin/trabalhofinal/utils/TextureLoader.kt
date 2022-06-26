package trabalhofinal.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture

class TextureLoader {
    val wall = Texture(Gdx.files.local("assets/textures/greystone.png"))
    val fungus = Texture(Gdx.files.local("assets/textures/mossy.png"))
    val web = Texture(Gdx.files.local("assets/textures/squareweb.png"))
    val pyro = Texture(Gdx.files.local("assets/textures/pyro.png"))
    val pyroLogo = Texture(Gdx.files.local("assets/textures/pyro_logo.png"))
    val recon = Texture(Gdx.files.local("assets/textures/recon.png"))
    val reconLogo = Texture(Gdx.files.local("assets/textures/recon_logo.png"))
    val botanist = Texture(Gdx.files.local("assets/textures/botanist.png"))
    val botanistLogo = Texture(Gdx.files.local("assets/textures/botanist_logo.png"))
    val egg = Texture(Gdx.files.local("assets/textures/barrel-no-bg.png"))
    val alien = Texture(Gdx.files.local("assets/textures/alien.png"))
}