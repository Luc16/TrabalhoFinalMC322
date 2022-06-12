package trabalhofinal.components

import com.badlogic.gdx.graphics.Texture

interface IRayCastTile: IPositioned {
    val id: Int
    val texture: Texture?
}