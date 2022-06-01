package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.MyGame
import trabalhofinal.WIDTH
import trabalhofinal.components.Tile
import trabalhofinal.utils.IVector2
import trabalhofinal.utils.MapReader
import kotlin.math.*

class RayCastingTestScreen(game: MyGame): CustomScreen(game) {

    private val tiles = mutableListOf<MutableList<Tile>>()

    private val player = Circle(WIDTH/2, HEIGHT/2, 10f)
    private var playerDir = Vector2(1f, 0f)
    private var cameraPlane = Vector2(0f, 0.66f)
    private var mapWidth = 0
    private var mapHeight = 0
    private var tileWidth = 0f
    private var tileHeight = 0f


    override fun show() {
        val reader = MapReader("assets/test.map")
        val mapString = reader.contents().reversed()
        mapWidth = mapString[0].length
        mapHeight = mapString.size

        tileWidth = WIDTH/mapWidth
        tileHeight = HEIGHT/mapHeight

        for (i in 0 until mapWidth){
            val line = mutableListOf<Tile>()
            for (j in 0 until mapHeight){
                val color = when (mapString[j][i]){
                    '1' -> Color.RED
                    '2' -> Color(0f, 40f/255f, 0f, 1f) // Verde mais escuro
                    '3' -> Color.PURPLE
                    '4' -> Color.WHITE
                    '5' -> Color.YELLOW
                    else -> Color.BLACK
                }
                line.add(
                    Tile(i*tileWidth, j*tileHeight, tileWidth, tileHeight, color)
                )
            }
            tiles.add(line)
        }

        Gdx.input.isCursorCatched = true

    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) Gdx.app.exit()
        tempController()
        val collisionWallsColors = multipleRayCast3D()
        val collisionPoints = mutableListOf<Vector2>()
        renderer.use(ShapeRenderer.ShapeType.Filled, viewport.camera.combined){
            collisionWallsColors.forEach{
                collisionPoints.add(it.first)
                renderer.color = it.third
                val rect = it.second
                renderer.rectLine(rect.x, rect.y, rect.x, rect.height, 1f)
            }

            // minimap
            renderer.color = Color.BLACK
            renderer.rect(5f, HEIGHT - 165f, 160f, 160f)
            renderer.color = Color.LIGHT_GRAY
            collisionPoints.forEach{
                renderer.rectLine(165f - player.x/5, HEIGHT - 165f + player.y/5, 165f - it.x/5, HEIGHT - 165f + it.y/5, 1f)
            }
            tiles.forEach{ line ->
                line.forEach { tile ->
                    if (tile.color != Color.BLACK){
                        renderer.color = tile.color
                        renderer.rect(165f - tileWidth/5 - tile.x/5, HEIGHT - 165f + tile.y/5, tile.width/5, tile.height/5)
                    }
                }
            }
            renderer.color = Color.BROWN
            renderer.circle(165f - player.x/5, HEIGHT - 165f + player.y/5, player.radius/5)


        }
    }

    private fun multipleRayCast3D(): List<Triple<Vector2, Rectangle, Color>>{
        val collisionsWallsColors = mutableListOf<Triple<Vector2, Rectangle, Color>>()
        for (x in 0 until WIDTH.toInt()){
            val cameraX = 2*x.toFloat()/ WIDTH - 1
            val rayDir = Vector2(
                playerDir.x + cameraPlane.x * cameraX,
                playerDir.y + cameraPlane.y * cameraX
            )
            val (tile, side, perpDist) = singleRayCast(rayDir)

            val h = HEIGHT
            val lineHeight = tileHeight*(h / perpDist)

            var drawStart = -lineHeight / 2 + h / 2
            if (drawStart < 0) drawStart = 0f
            var drawEnd = lineHeight / 2 + h / 2
            if (drawEnd >= h) drawEnd = h - 1

            val color = tile.color.cpy()
            if(side == 1) {
                color.r = color.r/2
                color.b = color.b/2
                color.g = color.g/2
            }

            collisionsWallsColors.add(
                Triple(
                    Vector2(player.x + rayDir.x*perpDist, player.y + rayDir.y*perpDist),
                    Rectangle(x.toFloat(), drawStart, 1f, drawEnd),
                    color
                )
            )
        }
        return collisionsWallsColors
    }

    private fun singleRayCast(dir: Vector2): Triple<Tile, Int, Float> {
        val rayStepSize = Vector2(
            tileWidth * abs(1 / dir.x),
            tileHeight * abs(1 / dir.y)
        )
        val mapPos = IVector2(
            (player.x / tileWidth).toInt(),
            (player.y / tileHeight).toInt()
        )
        val rayLengths = Vector2(0f, 0f)
        val step = IVector2(1, 1)

        if (dir.x < 0) {
            step.x = -1
            rayLengths.x = (player.x - mapPos.x*tileWidth) * rayStepSize.x / tileWidth
        } else
            rayLengths.x = (mapPos.x*tileWidth + tileWidth - player.x) * rayStepSize.x / tileWidth

        if (dir.y < 0) {
            step.y = -1
            rayLengths.y = (player.y - mapPos.y*tileHeight) * rayStepSize.y / tileHeight
        } else
            rayLengths.y = (mapPos.y*tileHeight + tileHeight - player.y) * rayStepSize.y / tileHeight

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

        return Triple(tiles[mapPos.x][mapPos.y], side,
            if (side == 0)
                rayLengths.x - rayStepSize.x
            else
                rayLengths.y - rayStepSize.y
        )

    }

    private fun rotate(vec: Vector2, angle: Float): Vector2{
        return Vector2(vec.x*cos(angle) - vec.y* sin(angle), vec.x*sin(angle) + vec.y* cos(angle))
    }

    private fun tempController(){
        val speed = 4
        val theta = 2*PI/180

        val deltaX = Gdx.input.deltaX.toFloat()/10
        playerDir = rotate(playerDir, deltaX*theta.toFloat())
        cameraPlane = rotate(cameraPlane, deltaX*theta.toFloat())

        Gdx.input.setCursorPosition((WIDTH/2).toInt(), (HEIGHT/2).toInt())

        if (Gdx.input.isKeyPressed(Input.Keys.W)) player.y += playerDir.y*speed
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player.y -= playerDir.y*speed

        var playerRect = Rectangle(player.x - player.radius, player.y - player.radius, 2*player.radius, 2*player.radius)
        tiles.forEach{ line ->
            line.forEach { tile ->
                if (tile.color != Color.BLACK && tile.overlaps(playerRect)){
                    if (Gdx.input.isKeyPressed(Input.Keys.S) && playerDir.y > 0 ||
                        Gdx.input.isKeyPressed(Input.Keys.W) && playerDir.y < 0)
                        player.y = tile.y + tile.height + player.radius
                    else
                        player.y = tile.y - player.radius
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) player.x += playerDir.x*speed
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player.x -= playerDir.x*speed

        playerRect = Rectangle(player.x - player.radius, player.y - player.radius, 2*player.radius, 2*player.radius)

        tiles.forEach{ line ->
            line.forEach { tile ->
                if (tile.color != Color.BLACK && tile.overlaps(playerRect)){
                    if (Gdx.input.isKeyPressed(Input.Keys.S) && playerDir.x > 0 ||
                        Gdx.input.isKeyPressed(Input.Keys.W) && playerDir.x < 0)
                        player.x = tile.x + tile.width + player.radius
                    else
                        player.x = tile.x - player.radius
                }
            }
        }
    }

}