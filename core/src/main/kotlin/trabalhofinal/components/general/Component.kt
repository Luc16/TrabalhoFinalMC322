package trabalhofinal.components.general

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture

interface Component {
    val isWall: Boolean
    val texture: Texture?
    var color: Color
    val type: ComponentType

    fun die() {}
}