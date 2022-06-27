package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.ComponentType
import trabalhofinal.components.general.RayCastTile
import trabalhofinal.components.general.RayCastComponent


class AlienWeb(tile: RayCastTile,
               tileWidth: Float, tileHeight: Float,
               texture: Texture,
               mapTexture: Texture,
): RayCastComponent(tile, tileWidth, tileHeight, texture, mapTexture) {
    override val type = ComponentType.WEB
}