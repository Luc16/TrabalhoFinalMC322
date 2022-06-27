package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.RayCastTile

class Recon(
    tile: RayCastTile,
    tileWidth: Float,
    tileHeight: Float,
    texture: Texture,
    mapTexture: Texture,
) : Player(tile, tileWidth, tileHeight, texture, mapTexture) {
    override val name: String
        get() = "Recon"
    override val maxEnergy: Int
        get() = 15
    override val webEnergy: Int
        get() = 4
    override val fungusEnergy: Int
        get() = 4
    override val eggEnergy: Int
        get() = 4
    override val cameraPlaneSize: Float
        get() = 0.66f

}