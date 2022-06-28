package trabalhofinal.components.general

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.components.TargetComponent
import trabalhofinal.utils.graphics.Textured2DMesh

abstract class RayCastComponent(
    tile: RayCastTile,
    private val tileWidth: Float, tileHeight: Float,
    override val texture: Texture,
    val mapTexture: Texture,
    ):
    Disposable,
    Comparable<RayCastComponent>,
    Component,
    MapBatchDrawable
{
    var tile: RayCastTile = tile
        set(value) {
            value.component = this
            field = value
        }
    var x = tile.i*tileWidth + tileWidth/2
    var y = tile.j*tileHeight + tileHeight/2
    override val isWall = false
    override var color: Color = Color.BLACK
    var seen = false

    private var mesh: Textured2DMesh? = null
    private var dist = Float.MAX_VALUE

    open fun update(player: Player, zBuffer: List<Float>, tileWidth: Float, tileHeight: Float){
        dist = (player.x - x)*(player.x - x) + (player.y - y)*(player.y - y)

        // variavel para aumentar ou diminuir os sprites

        // posição relativa do jogador com o componente (utilizando uma matriz de mudança de coordenadas)
        val transformedPos = Vector2(x - player.x, y - player.y)
        val invDet = 1f / (player.cameraPlane.x * player.dir.y - player.dir.x * player.cameraPlane.y)
        transformedPos.set(
            invDet * (player.dir.y * transformedPos.x - player.dir.x * transformedPos.y),
            invDet * (-player.cameraPlane.y * transformedPos.x + player.cameraPlane.x * transformedPos.y)
        )
        if (transformedPos.y == 0f) return

        val h = 1.5f*HEIGHT
        val spriteScreenX = (WIDTH / 2) * (1 + transformedPos.x / transformedPos.y)
        val spriteHeight = tileHeight * h / transformedPos.y
        val drawStartY =  -spriteHeight / 2 + h / 2
        // val drawEndY = spriteHeight / 2 + h / 2

        val spriteWidth = tileWidth * h / transformedPos.y
        var drawStartX = -spriteWidth / 2 + spriteScreenX
        var drawEndX = spriteWidth / 2 + spriteScreenX

        // verifica se o componente está totalmente fora da tela
        if(drawStartX >= WIDTH || drawEndX < 0 || transformedPos.y < 0) {
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

        if (WIDTH/2 in drawStartX..drawEndX && player.targetComponent.dist > transformedPos.y)
            player.targetComponent = TargetComponent(this, transformedPos.y)

        // calcula posição na textura
        val uStart = (drawStartX - spriteScreenX)/spriteWidth + 0.5f
        val uEnd = (drawEndX - spriteScreenX)/spriteWidth + 0.5f

        // cria a mesh para ser desenhada
        mesh?.dispose()
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

    open fun render(shader: ShaderProgram, initialX: Float = 0f, initialY: Float = 0f, ratio: Float = 1f){
        if (!seen || mesh == null) return
        mesh?.run {
            moveAndScale(initialX, initialY, ratio)
            standAloneRender(shader)
        }
    }

    override fun draw(startX: Float, startY: Float, ratio: Float, batch: Batch) {
        if (!seen) return
        batch.draw(
            mapTexture,
            startX - (x + tileWidth/2) * ratio, startY + (y - tileWidth/2) * ratio,
            tileWidth * ratio, tileWidth*ratio
        )
    }

    override fun die(){
        tile.component = null
    }

    override fun compareTo(other: RayCastComponent): Int {
        val diff = dist - other.dist
        return if (diff > 0) 1 else if (diff < 0) -1 else 0
    }

    override fun dispose() {
        mesh?.dispose()
    }
}

