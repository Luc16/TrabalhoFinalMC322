package trabalhofinal.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.components.Player
import trabalhofinal.components.Tile
import kotlin.math.abs

class RayCaster(
    private val tiles: List<List<Tile>>,
    private val tileWidth: Float,
    private val tileHeight: Float
) {

    fun multipleRayCast3D(player: Player): List<Triple<Vector2, Rectangle, Color>> {
        val collisionsWallsColors = mutableListOf<Triple<Vector2, Rectangle, Color>>()
        for (x in 0 until WIDTH.toInt()) {
            val cameraX = 2 * x.toFloat() / WIDTH - 1
            val rayDir = Vector2(
                player.dir.x + player.cameraPlane.x * cameraX,
                player.dir.y + player.cameraPlane.y * cameraX
            )
            val (tile, side, perpDist) = singleRayCast(player, rayDir)

            val h = HEIGHT
            val lineHeight = tileHeight * (h / perpDist)

            var drawStart = -lineHeight / 2 + h / 2
            if (drawStart < 0) drawStart = 0f
            var drawEnd = lineHeight / 2 + h / 2
            if (drawEnd >= h) drawEnd = h - 1

            val color = tile.color.cpy()
            if (side == 1) {
                color.r = color.r / 2
                color.b = color.b / 2
                color.g = color.g / 2
            }

            collisionsWallsColors.add(
                Triple(
                    Vector2(player.x + rayDir.x * perpDist, player.y + rayDir.y * perpDist),
                    Rectangle(x.toFloat(), drawStart, 1f, drawEnd),
                    color
                )
            )
        }
        return collisionsWallsColors
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

            if (tiles[mapPos.x][mapPos.y].color != Color.BLACK) hit = true

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