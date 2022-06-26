package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.RayCastTile

class Botanist(
    tile: RayCastTile,
    tileWidth: Float,
    tileHeight: Float,
    texture: Texture,
    mapTexture: Texture,
    color: Color = Color.GREEN
) : Player(tile, tileWidth, tileHeight, texture, mapTexture, color) {
    override val name: String
        get() = "Botanist"
    override val maxEnergy: Int
        get() = 10
    override val webEnergy: Int
        get() = 4
    override val fungusEnergy: Int
        get() = 2
    override val eggEnergy: Int
        get() = 4

}