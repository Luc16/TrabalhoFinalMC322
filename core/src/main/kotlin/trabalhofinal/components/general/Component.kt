package trabalhofinal.components.general

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.ComponentType

interface Component {
    val isWall: Boolean
    val texture: Texture?
    var color: Color
    val type: ComponentType
}