package trabalhofinal.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Vector2
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.components.IRayCastTile
import trabalhofinal.components.Player
import trabalhofinal.utils.graphics.MeshGroup
import trabalhofinal.utils.graphics.Textured2DMesh
import kotlin.math.abs
import kotlin.math.max

class RayCaster(
    private val tiles: List<List<IRayCastTile>>,
    private val tileWidth: Float,
    private val tileHeight: Float,
) {

    lateinit var zBuffer: List<Float>
        private set
    lateinit var collisionPoints: List<Vector2>
        private set
    lateinit var meshes: MeshGroup
        private set
    var floorLevel = 0f
        private set

    fun multipleRayCast3D(player: Player) {
        // variaveis das informações do resultado
        val collisionPoints = mutableListOf<Vector2>()
        val meshes = MeshGroup()
        val zBuffer = Array(WIDTH.toInt()) {0f}

        var rayDir = Vector2() // direcao atual do raio
        var tile: IRayCastTile
        var prevTile: IRayCastTile? = null
        var initialVertices = listOf<Vector2>() // vertices posicionais iniciais da mesh
        // inicio e fim da coordenada y da mesh
        var drawStart = 0f
        var drawEnd = 0f
        // lado em que o raio bate na parede
        var side = 0
        var prevSide = -1
        // x real do tile analizado no momento
        var wallX = 0f
        var startWallX = 0f
        var prevWallX = 0f

        for (x in 0 until WIDTH.toInt()) {
            val cameraX = 2 * x.toFloat() / WIDTH - 1
            // calcula a direção do raio atual
            rayDir = Vector2(
                player.dir.x + player.cameraPlane.x * cameraX,
                player.dir.y + player.cameraPlane.y * cameraX
            )
            // armazena o resultado de um rayCast
            val result = singleRayCast(player, rayDir)
            tile = result.first
            side = result.second
            val perpDist = result.third
            if (x == WIDTH.toInt()/2) player.aimingComponent = Pair(tile.component!!, perpDist)
            zBuffer[x] = perpDist

            // calcula altura da linha
            val h = 1.5f * HEIGHT
            val lineHeight = tileHeight * (h / perpDist)

            val prevDrawStartAndEnd = Pair(drawStart, drawEnd)

            drawStart = -lineHeight / 2 + h / 2
            floorLevel = max(drawStart, floorLevel)
            drawEnd = lineHeight / 2 + h / 2
            prevWallX = wallX
            wallX = if (side == 0) player.y + rayDir.y * perpDist else player.x + rayDir.x * perpDist

            val curX = x.toFloat()

            if (prevTile == null || prevTile != tile || prevSide != side) {
                // quando a muda o tile ou o lado analizado fecha a mesh e comeca outra

                if (prevTile != null) {
                    val (uStart, uEnd) = calculateUStartAndUEnd(prevTile, rayDir, startWallX, prevWallX,
                        tileWidth, tileHeight, prevSide)
                    meshes.add(
                        createTextured2DMesh(
                            prevTile.texture!!, initialVertices, curX,
                            prevDrawStartAndEnd.first, prevDrawStartAndEnd.second,
                            uStart, uEnd, prevSide
                        )
                    )
                }
                initialVertices = listOf(
                    Vector2(curX, drawStart),
                    Vector2(curX, drawEnd)
                )
                prevTile = tile
                prevSide = side
                startWallX = wallX
            }
            collisionPoints.add(Vector2(player.x + rayDir.x * perpDist, player.y + rayDir.y * perpDist))
        }
        if (prevTile != null) {
            // cria a ultima mesh
            val (uStart, uEnd) = calculateUStartAndUEnd(prevTile, rayDir, startWallX, prevWallX,
                tileWidth, tileHeight, prevSide)

            meshes.add(
                createTextured2DMesh(prevTile.texture!!, initialVertices,
                    WIDTH, drawStart, drawEnd, uStart, uEnd, side)
            )
        }
        this.zBuffer = zBuffer.toList()
        this.collisionPoints = collisionPoints
        this.meshes = meshes
    }

    private fun calculateUStartAndUEnd(
        prevTile: IRayCastTile,
        rayDir: Vector2,
        startWallX: Float,
        prevWallX: Float,
        tileWidth: Float,
        tileHeight: Float,
        prevSide: Int
    ): Pair<Float, Float> = Pair(
        // calcula a posição da textura sendo vista
        if (prevSide == 0)
            abs((if (rayDir.x < 0) 1f else 0f) - abs(prevTile.y - startWallX) / tileHeight)
        else
            abs((if (rayDir.y > 0) 1f else 0f) - abs(prevTile.x - startWallX) / tileWidth),
        if (prevSide == 0)
            abs((if (rayDir.x > 0) 1f else 0f) - abs(prevTile.y + tileHeight - prevWallX) / tileHeight)
        else
            abs((if (rayDir.y < 0) 1f else 0f) - abs(prevTile.x + tileWidth - prevWallX) / tileWidth)
    )

    private fun createTextured2DMesh(
        texture: Texture,
        initialVertices: List<Vector2>,
        x: Float,
        drawStart: Float,
        drawEnd: Float,
        uStart: Float,
        uEnd: Float,
        side: Int
    ): Textured2DMesh = Textured2DMesh(
        texture, floatArrayOf(
            initialVertices[1].x, initialVertices[1].y, uStart, 0f,//upper left
            x, drawEnd, uEnd, 0f, //upper right
            x, drawStart, uEnd, 1f, //lower right
            initialVertices[0].x, initialVertices[0].y, uStart, 1f, //lower left
        ), if (side == 1) 2f else 1f
    )


    private fun singleRayCast(player: Player, rayDir: Vector2): Triple<IRayCastTile, Int, Float> {
        // quanto o vetor anda em x e em y
        val rayStepSize = Vector2(
            tileWidth * abs(1 / rayDir.x),
            tileHeight * abs(1 / rayDir.y)
        )
        // posição do vetor no mapa
        val mapPos = IVector2(
            (player.x / tileWidth).toInt(),
            (player.y / tileHeight).toInt()
        )
        val rayLengths = Vector2(0f, 0f)
        val step = IVector2(1, 1)

        // considerando o local real do player, e sua distancia a borda de tile mais proxima
        if (rayDir.x < 0) {
            step.i = -1
            rayLengths.x = (player.x - mapPos.i * tileWidth) * rayStepSize.x / tileWidth
        } else
            rayLengths.x = (mapPos.i * tileWidth + tileWidth - player.x) * rayStepSize.x / tileWidth

        if (rayDir.y < 0) {
            step.j = -1
            rayLengths.y = (player.y - mapPos.j * tileHeight) * rayStepSize.y / tileHeight
        } else
            rayLengths.y = (mapPos.j * tileHeight + tileHeight - player.y) * rayStepSize.y / tileHeight

        var side = 0

        while (!tiles[mapPos.i][mapPos.j].isWall) {
            // realiza o raycast
            if (rayLengths.x < rayLengths.y) {
                mapPos.i += step.i
                side = 0
                rayLengths.x += rayStepSize.x
            } else {
                mapPos.j += step.j
                side = 1
                rayLengths.y += rayStepSize.y
            }

        }

        return Triple(
            tiles[mapPos.i][mapPos.j], side,
            if (side == 0)
                rayLengths.x - rayStepSize.x
            else
                rayLengths.y - rayStepSize.y
        )

    }
}