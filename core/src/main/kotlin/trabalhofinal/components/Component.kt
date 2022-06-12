package trabalhofinal.components

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.utils.graphics.Textured2DMesh
import kotlin.math.max
import kotlin.math.min

class Component(private val texture: Texture, val pos: Vector2) {
    var seen = false
    lateinit var tile: Tile
    private lateinit var mesh: Textured2DMesh

    fun createMesh(player: Player, zBuffer: List<Float>, tileWidth: Float, tileHeight: Float){
        // variavel para aumentar ou diminuir os sprites
        val div = 1f

        // posição relativa do jogador com o componente (utilizando uma matriz de mudança de coordenadas)
        val transformedPos = Vector2(pos.x - player.x, pos.y - player.y)
        val invDet = 1f / (player.cameraPlane.x * player.dir.y - player.dir.x * player.cameraPlane.y)
        transformedPos.set(
            invDet * (player.dir.y * transformedPos.x - player.dir.x * transformedPos.y),
            invDet * (-player.cameraPlane.y * transformedPos.x + player.cameraPlane.x * transformedPos.y)
        )

        val h = 1.5f*HEIGHT
        val spriteScreenX = (WIDTH / 2) * (1 + transformedPos.x / transformedPos.y)
        val spriteHeight = (tileHeight * h / transformedPos.y)/div
        val drawStartY =  -spriteHeight / 2 + h / 2
        // val drawEndY = spriteHeight / 2 + h / 2

        val spriteWidth = (tileWidth * h / transformedPos.y)/div
        var drawStartX = -spriteWidth / 2 + spriteScreenX
        var drawEndX = spriteWidth / 2 + spriteScreenX

        // verifica se o componente está totalmente fora da tela
        if(drawStartX > WIDTH || drawEndX < 0 || transformedPos.y < 0) {
            seen = false
            return
        }

        // procura o valor inicial do componente em X que não está atras de algo
        while (drawStartX < 0 || transformedPos.y > zBuffer[drawStartX.toInt()]){
            drawStartX++
            if (drawStartX >= drawEndX || drawStartX >= WIDTH) {
                seen = false
                return
            }
        }
        drawStartX--

        // procura o valor final do componente em X que não está atras de algo
        while (drawEndX >= WIDTH || transformedPos.y > zBuffer[drawEndX.toInt()]){
            drawEndX--
            if (drawEndX <= drawStartX) {
                seen = false
                return
            }
        }
        drawEndX++

        // calcula posição na textura
        val uStart = (drawStartX - spriteScreenX)/spriteWidth + 0.5f
        val uEnd = (drawEndX - spriteScreenX)/spriteWidth + 0.5f

        // cria a mesh para ser desenhada
        mesh = Textured2DMesh(texture,
            floatArrayOf(
                drawStartX, drawStartY + spriteHeight, uStart, 0f,//upper left
                drawEndX, drawStartY + spriteHeight, uEnd, 0f, //upper right
                drawEndX, drawStartY, uEnd, 1f, //lower right
                drawStartX, drawStartY, uStart, 1f, //lower left
            )
        )
        seen = true
    }

    fun render(shader: ShaderProgram, initialX: Float = 0f, initialY: Float = 0f, ratio: Float = 1f){
        if (!seen) return

        mesh.moveAndScale(initialX, initialY, ratio)
        mesh.standAloneRender(shader)
        mesh.dispose()
    }
}