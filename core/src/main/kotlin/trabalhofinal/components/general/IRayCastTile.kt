package trabalhofinal.components.general

import com.badlogic.gdx.graphics.Texture

interface IRayCastTile: IStatic {
    val isWall: Boolean
    val texture: Texture?
    var component: Component?
    val i: Int
    val j: Int
}