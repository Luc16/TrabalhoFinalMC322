package trabalhofinal.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

class Button(
    private val message: String,
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float,
    val onRelease: () -> Unit,
    private val normalColor: Color = Color(255f/255,159f/255,41f/255,1f),
    private val hoverColor: Color = Color(255f/(1.5f*255),159f/(1.5f*255),41f/(1.5f*255),1f),
    private val clickColor: Color = Color(255f/(2*255),159f/(2*255),41f/(2*255),1f),
): Rectangle(centerX - width/2, centerY - height/2, width, height) {

    private var color = normalColor
    var hovered = false
    private set

    fun checkHover(mouse: Vector2){
        if (contains(mouse)) onHover()
        else resetColor()
    }
    private fun onHover() {
        hovered = true
        color = hoverColor
    }
    fun onPress() { if(hovered) color = clickColor }
    fun resetColor() {
        hovered = false
        color = normalColor
    }

    fun drawMessage(batch: Batch, font: BitmapFont){
        val screenText = GlyphLayout(font, message)
        font.draw(batch, screenText, x + width/2 - screenText.width/2, y + height/2 + screenText.height/2)
    }

    fun drawRect(renderer: ShapeRenderer){
        renderer.color = color
        renderer.rect(x, y, width, height)
    }



}