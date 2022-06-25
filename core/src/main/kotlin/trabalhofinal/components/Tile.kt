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
    ) :
    IMapDrawable,
    IRayCastTile,
    Node(i, j, false) {
    override val x: Float = i*width
    override val y: Float = j*height
    override var isWall: Boolean = false
    override var component: Component? = null
        set(value) {
            notTraversable = value != null
            field = value
        }

    override val texture: Texture?
        get() = component?.texture

    fun setInitialComponent(comp: Component?) {
        component = comp
        isWall = comp?.isWall ?: false
    }
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