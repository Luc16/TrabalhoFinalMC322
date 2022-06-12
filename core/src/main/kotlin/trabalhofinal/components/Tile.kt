package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import trabalhofinal.utils.Node

class Tile(
    i: Int,
    j: Int,
    override val width: Float,
    override val height: Float,
    override var color: Color,
    override val texture: Texture?,
    override val id: Int
    ) :
    IRayCastTile,
    IMapDrawable,
    Node(i, j, id != 0) {
    override val x: Float = i*width
    override val y: Float = j*height

    //temporario!!!!!!!!!!!!!!!!
    val r = Rectangle(x, y, width, height)

    override fun draw(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer) {
        renderer.color = color
        renderer.rect(
            startX - width*ratio - x*ratio,
            startY + y*ratio,
            width*ratio, height*ratio)
    }

}