package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture

class Wall(
        override var color: Color,
        override val texture: Texture?,
    ): Component {
    override val isWall = true
    override val type = ComponentType.WALL
}