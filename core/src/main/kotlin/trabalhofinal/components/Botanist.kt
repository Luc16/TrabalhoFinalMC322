package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.IRayCastTile

class Botanist(
    tile: IRayCastTile,
    radius: Float,
    tileWidth: Float,
    tileHeight: Float,
    texture: Texture,
    color: Color = Color.GREEN
) : Player(tile, radius, tileWidth, tileHeight, texture, color) {
    override val name: String
        get() = TODO("Not yet implemented")
    override val maxEnergy: Int
        get() = 10
    override val webEnergy: Int
        get() = 4
    override val fungusEnergy: Int
        get() = 2
    override val eggEnergy: Int
        get() = 4

}