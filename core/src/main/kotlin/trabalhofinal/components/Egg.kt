package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.ComponentType
import trabalhofinal.components.general.RayCastTile
import trabalhofinal.components.general.RayCastComponent


class Egg(tile: RayCastTile,
          tileWidth: Float, tileHeight: Float,
          texture: Texture,
          color: Color = Color.BROWN,
): RayCastComponent(tile, tileWidth, tileHeight, texture, color) {
    override val type = ComponentType.EGG
}