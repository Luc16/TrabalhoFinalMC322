package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.RayCastTile

class Pyro(
    tile: RayCastTile,
    tileWidth: Float,
    tileHeight: Float,
    texture: Texture,
    color: Color = Color.RED
) : Player(tile, tileWidth, tileHeight, texture, color) {
    override val name: String
        get() = "Pyro"
    override val maxEnergy: Int
        get() = 10
    override val webEnergy: Int
        get() = 2
    override val fungusEnergy: Int
        get() = 4
    override val eggEnergy: Int
        get() = 4

}