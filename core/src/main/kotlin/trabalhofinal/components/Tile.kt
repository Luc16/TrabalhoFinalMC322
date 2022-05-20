package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

class Tile(x: Float, y: Float, width: Float, height: Float, var color: Color): Rectangle(x, y, width, height) {

    fun draw(renderer: ShapeRenderer){
        renderer.color = color
        renderer.rect(x + 1f, y + 1f, width - 1f, height - 1f)
    }
}