package trabalhofinal.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.utils.graphics.Textured2DMesh
import kotlin.math.abs

class Component(private val texture: Texture, val pos: Vector2) {
    var seen = false
    lateinit var tile: Tile

    fun render(player: Player, zBuffer: Array<Float>, shader: ShaderProgram, tileWidth: Float, tileHeight: Float){
        val div = 1f

        val transformedPos = Vector2(pos.x - player.x, pos.y - player.y)
        val invDet = 1f / (player.cameraPlane.x * player.dir.y - player.dir.x * player.cameraPlane.y)
        transformedPos.set(
            invDet * (player.dir.y * transformedPos.x - player.dir.x * transformedPos.y),
            invDet * (-player.cameraPlane.y * transformedPos.x + player.cameraPlane.x * transformedPos.y)
        )
        val h = 1.5f*HEIGHT
        val spriteScreenX = (WIDTH / 2) * (1 + transformedPos.x / transformedPos.y)
        val spriteHeight = (tileHeight * h / transformedPos.y)/div
        val drawStartY = -spriteHeight / 2 + h / 2
        val drawEndY = spriteHeight / 2 + h / 2

        //calculate width of the sprite
        val spriteWidth = (tileWidth * h / transformedPos.y)/div
        var drawStartX = -spriteWidth / 2 + spriteScreenX
        var drawEndX = spriteWidth / 2 + spriteScreenX


        if(drawStartX > WIDTH || drawEndX < 0 || transformedPos.y < 0) {
            seen = false
            return
        }

        while (drawStartX < 0 || transformedPos.y > zBuffer[drawStartX.toInt()]){
            drawStartX++
            if (drawStartX >= drawEndX || drawStartX >= WIDTH) {
                seen = false
                return
            }
        }
        drawStartX--

        while (drawEndX >= WIDTH || transformedPos.y > zBuffer[drawEndX.toInt()]){
            drawEndX--
            if (drawEndX <= drawStartX) {
                seen = false
                return
            }
        }
        drawEndX++

        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            println(player.dir)
            println(HEIGHT*tileHeight/transformedPos.y)
        }
        val y = drawStartY
        val uStart = (drawStartX - spriteScreenX)/spriteWidth + 0.5f
        val uEnd = (drawEndX - spriteScreenX)/spriteWidth + 0.5f

        val tex = Textured2DMesh(texture,
            floatArrayOf(
                drawStartX, y + spriteHeight, uStart, 0f,//upper left
                drawEndX, y + spriteHeight, uEnd, 0f, //upper right
                drawEndX, y, uEnd, 1f, //lower right
                drawStartX, y, uStart, 1f, //lower left
            )
        )
        tex.standAloneRender(shader)
        tex.dispose()
        seen = true

    }
}