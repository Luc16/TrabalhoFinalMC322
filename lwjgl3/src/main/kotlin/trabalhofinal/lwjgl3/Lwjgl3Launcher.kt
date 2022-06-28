package trabalhofinal.lwjgl3

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.MyGame


fun main() {
    Lwjgl3Application(MyGame(), Lwjgl3ApplicationConfiguration().apply {
        setTitle("Alien Nest Cleaners")
        setWindowedMode(WIDTH.toInt(), HEIGHT.toInt())
        setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png")
    })
}
