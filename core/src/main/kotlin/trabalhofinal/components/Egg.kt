package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.IRayCastTile
import trabalhofinal.components.general.RayCastComponent


class Egg(tile: IRayCastTile, val radius: Float, //TODO tirar
          tileWidth: Float, tileHeight: Float,
          texture: Texture,
          color: Color = Color.GREEN,
) : RayCastComponent(texture, tile.i*tileWidth + tileWidth/2, tile.j*tileHeight + tileHeight/2, color,  tile, ComponentType.ALIEN){
}