package trabalhofinal.components

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
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
    var component: Component? = component
        set(value) {
            notTraversable = value != null
            field = value
        }
    override val texture: Texture?
        get() = component?.texture

    //temporario!!!!!!!!!!!!!!!!
    val r = Rectangle(x, y, width, height)

    override fun draw(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer) {
        component?.let {
            renderer.color = it.color
            renderer.rect(
                startX - width * ratio - x * ratio,
                startY + y * ratio,
                width * ratio, height * ratio
            )
        }
    }

}