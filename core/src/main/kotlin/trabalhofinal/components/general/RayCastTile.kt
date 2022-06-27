package trabalhofinal.components.general

import com.badlogic.gdx.graphics.Texture

interface RayCastTile {
    val x: Float
    val y: Float
    var isWall: Boolean
    val texture: Texture?
    var component: Component?
    var i: Int
    var j: Int
}