package trabalhofinal.components

import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.Player
import trabalhofinal.components.general.RayCastTile

class Botanist(
    tile: RayCastTile,
    tileWidth: Float,
    tileHeight: Float,
    texture: Texture,
    mapTexture: Texture,
) : Player(tile, tileWidth, tileHeight, texture, mapTexture) {
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
    override val cameraPlaneSize: Float
        get() = 0.40f
}