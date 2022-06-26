package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import trabalhofinal.components.general.*
import trabalhofinal.utils.Node

class Tile(
        i: Int,
        j: Int,
        val width: Float,
        val height: Float,
    ) :
    DrawableTile,
    RayCastTile,
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

    private fun drawRect(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer) = renderer.rect(
        startX - width * ratio - x * ratio,
        startY + y * ratio,
        width * ratio, height * ratio
    )

    override fun draw(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer) {
        if (component?.type == ComponentType.PLAYER || component == null) return
        renderer.color = component?.color
        drawRect(startX, startY, ratio, renderer)
    }

    override fun drawOutline(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer) {
        renderer.color = Color.WHITE
        drawRect(startX, startY, ratio, renderer)
    }

}