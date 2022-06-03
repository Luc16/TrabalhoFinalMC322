package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle

class Tile(x: Float, y: Float, width: Float, height: Float, val color: Color, val texture: Texture?, val id: Int): Rectangle(x, y, width, height) {

    fun draw(batch: SpriteBatch){
        if (texture != null)
            batch.draw(texture, x, y, width, height)
    }
}