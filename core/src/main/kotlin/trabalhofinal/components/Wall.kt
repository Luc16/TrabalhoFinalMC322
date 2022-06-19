package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.Component

class Wall(
        override var color: Color,
        override val texture: Texture?,
    ): Component {
    override val isWall = true
    override val type = ComponentType.WALL
}