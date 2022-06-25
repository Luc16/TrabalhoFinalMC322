package trabalhofinal.components

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import trabalhofinal.components.general.Component
import trabalhofinal.components.general.IMapDrawable
import trabalhofinal.components.general.IRayCastTile
import trabalhofinal.utils.Node

class Tile(
        i: Int,
        j: Int,
        val width: Float,
        val height: Float,
        component: Component? = null
    ) :
    IMapDrawable,
    IRayCastTile,
    Node(i, j, component != null) {
    override val x: Float = i*width
    override val y: Float = j*height
    override val isWall: Boolean = component?.isWall ?: false
    override var component: Component? = component
        set(value) {
            notTraversable = value != null
            field = value
        }

    override val texture: Texture?
        get() = component?.texture

    override fun draw(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer) {
        component?.let {
            renderer.color = it.color
            renderer.rect(
                startX - width * ratio - x * ratio-1,
                startY + y * ratio -1,
                width * ratio - 1, height * ratio -1
            )
        }
    }

}