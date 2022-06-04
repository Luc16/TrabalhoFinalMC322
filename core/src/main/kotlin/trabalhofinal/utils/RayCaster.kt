package trabalhofinal.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector2
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.components.Player
import trabalhofinal.components.Tile
import trabalhofinal.utils.graphics.QuadGroup
import trabalhofinal.utils.graphics.Textured2DMesh
import kotlin.math.abs

class RayCaster(
    private val tiles: List<List<Tile>>,
    private val tileWidth: Float,
    private val tileHeight: Float,
    private val shader: ShaderProgram
) {

    fun multipleRayCast3D(player: Player): Pair<List<Vector2>, QuadGroup> {
        val collisionPoints = mutableListOf<Vector2>()
        val quads = QuadGroup(shader)
        var rayDir = Vector2()
        var tile: Tile
        var prevTile: Tile? = null
        var initialVertices = listOf<Vector2>()
        var drawStart = 0f
        var drawEnd = 0f
        var side = 0
        var prevSide = -1
        var wallX = 0f
        var startWallX = 0f
        var prevWallX = 0f

        for (x in 0 until WIDTH.toInt()) {
            val cameraX = 2 * x.toFloat() / WIDTH - 1
            rayDir = Vector2(
                player.dir.x + player.cameraPlane.x * cameraX,
                player.dir.y + player.cameraPlane.y * cameraX
            )
            val result = singleRayCast(player, rayDir)
            tile = result.first
            side = result.second
            val perpDist = result.third

            val h = 1.5f * HEIGHT
            val lineHeight = tileHeight * (h / perpDist)

            val prevDrawStartAndEnd = Pair(drawStart, drawEnd)

            drawStart = -lineHeight / 2 + h / 2
            drawEnd = lineHeight / 2 + h / 2
            prevWallX = wallX
            wallX = if (side == 0) player.y + rayDir.y * perpDist else player.x + rayDir.x * perpDist

            if (prevTile == null || prevTile != tile || prevSide != side) {
                if (prevTile != null) {
                    val uStart = if (prevSide == 0)
                        abs((if (rayDir.x < 0) 1f else 0f) - abs(prevTile.y - startWallX) / tile.height)
                    else
                        abs((if (rayDir.y > 0) 1f else 0f) - abs(prevTile.x - startWallX) / tile.width)

                    val uEnd = if (prevSide == 0)
                        abs((if (rayDir.x > 0) 1f else 0f) - abs(prevTile.y + tile.height - prevWallX) / tile.height)
                    else
                        abs((if (rayDir.y < 0) 1f else 0f) - abs(prevTile.x + tile.width - prevWallX) / tile.width)

                    quads.add(
                        createTextured2DQuad(prevTile.texture!!, initialVertices, x.toFloat(),
                            prevDrawStartAndEnd.first, prevDrawStartAndEnd.second, uStart, uEnd, prevSide)
                    )
                }
                initialVertices = listOf(
                    Vector2(x.toFloat(), drawStart),
                    Vector2(x.toFloat(), drawEnd)
                )
                prevTile = tile
                prevSide = side
                startWallX = wallX
            }
            collisionPoints.add(Vector2(player.x + rayDir.x * perpDist, player.y + rayDir.y * perpDist))
        }
        if (prevTile != null) {
            val uStart = if (prevSide == 0)
                abs((if (rayDir.x < 0) 1f else 0f) - abs(prevTile.y - startWallX) / tileHeight)
            else
                abs((if (rayDir.y > 0) 1f else 0f) - abs(prevTile.x - startWallX) / tileWidth)

            val uEnd = if (prevSide == 0)
                abs((if (rayDir.x > 0) 1f else 0f) - abs(prevTile.y + tileHeight - prevWallX) / tileHeight)
            else
                abs((if (rayDir.y < 0) 1f else 0f) - abs(prevTile.x + tileWidth - prevWallX) / tileWidth)

            quads.add(
                createTextured2DQuad(prevTile.texture!!, initialVertices, WIDTH, drawStart, drawEnd, uStart, uEnd, side)
            )
        }
        return Pair(collisionPoints, quads)
    }

    private fun createTextured2DQuad(
        texture: Texture, initialVertices: List<Vector2>,
        x: Float, drawStart: Float, drawEnd: Float,
        uStart: Float, uEnd: Float, side: Int
    ): Textured2DMesh {

        fun mean(f1: Float, f2: Float) = (f1 + f2)/2

        val meanU = mean(uStart, uEnd)
        return Textured2DMesh(
            texture, floatArrayOf(
                initialVertices[1].x, initialVertices[1].y, uStart, 0f,//upper left
                initialVertices[1].x, mean(initialVertices[1].y, initialVertices[0].y), uStart, 0.5f, //middle left
                mean(initialVertices[1].x, x), mean(initialVertices[1].y, drawEnd), meanU, 0f, //upper middle
                x, drawEnd, uEnd, 0f, //upper right
                x, mean(drawEnd, drawStart), uEnd, 0.5f, //middle right
                x, drawStart, uEnd, 1f, //lower right
                mean(initialVertices[0].x, x), mean(initialVertices[0].y, drawStart), meanU, 1f, //upper middle
                initialVertices[0].x, initialVertices[0].y, uStart, 1f, //lower left
            ), if (side == 1) 2f else 1f
        )
    }

    private fun singleRayCast(player: Player, rayDir: Vector2): Triple<Tile, Int, Float> {
        val rayStepSize = Vector2(
            tileWidth * abs(1 / rayDir.x),
            tileHeight * abs(1 / rayDir.y)
        )
        val mapPos = IVector2(
            (player.x / tileWidth).toInt(),
            (player.y / tileHeight).toInt()
        )
        val rayLengths = Vector2(0f, 0f)
        val step = IVector2(1, 1)

        if (rayDir.x < 0) {
            step.x = -1
            rayLengths.x = (player.x - mapPos.x * tileWidth) * rayStepSize.x / tileWidth
        } else
            rayLengths.x = (mapPos.x * tileWidth + tileWidth - player.x) * rayStepSize.x / tileWidth

        if (rayDir.y < 0) {
            step.y = -1
            rayLengths.y = (player.y - mapPos.y * tileHeight) * rayStepSize.y / tileHeight
        } else
            rayLengths.y = (mapPos.y * tileHeight + tileHeight - player.y) * rayStepSize.y / tileHeight

        var hit = false
        var side = 0
        while (!hit) {
            if (rayLengths.x < rayLengths.y) {
                mapPos.x += step.x
                side = 0
                rayLengths.x += rayStepSize.x
            } else {
                mapPos.y += step.y
                side = 1
                rayLengths.y += rayStepSize.y
            }

            if (tiles[mapPos.x][mapPos.y].id != 0) hit = true

        }

        return Triple(
            tiles[mapPos.x][mapPos.y], side,
            if (side == 0)
                rayLengths.x - rayStepSize.x
            else
                rayLengths.y - rayStepSize.y
        )

    }
}