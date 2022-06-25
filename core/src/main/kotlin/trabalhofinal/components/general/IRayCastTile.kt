package trabalhofinal.components.general

import com.badlogic.gdx.graphics.Texture

interface IRayCastTile: IStatic {
    var isWall: Boolean
    val texture: Texture?
    var component: Component?
    var i: Int
    var j: Int
}