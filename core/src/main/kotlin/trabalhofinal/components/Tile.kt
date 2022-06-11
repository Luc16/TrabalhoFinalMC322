package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

class Tile(
    i: Int,
    j: Int,
    var width: Float,
    var height: Float,
    var color: Color,
    val texture: Texture?,
    val id: Int
    ) :
    Node(i, j, id != 0) {
    val x: Float = i*width
    val y: Float = j*height

    //temporario!!!!!!!!!!!!!!!!
    val r = Rectangle(x, y, width, height)

    fun draw(batch: SpriteBatch) {
        if (texture != null)
            batch.draw(texture, x, y, width, height)
    }
}