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
import kotlin.random.Random

fun randomColor(offset: Float) = Color(
    offset + Random.nextFloat(),
    offset + Random.nextFloat(),
    offset + Random.nextFloat(), 1f
)

class TestScreen(game: MyGame): CustomScreen(game) {

    private val tiles = mutableListOf<MutableList<Tile>>()
//    private val text = GlyphLayout()

    private val player = Circle(WIDTH/2, HEIGHT/2, 10f)
    private var playerDir = Vector2(1f, 0f)
    private val cameraPlane = Vector2(0f, 0.66f)
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
                line.add(
                    Tile(i*tileWidth, j*tileHeight, tileWidth, tileHeight, if (mapString[j][i] == '1') Color.WHITE else Color.BLUE)
                )
            }
            tiles.add(line)
        }

    }

    override fun render(delta: Float) {
//        text.setText(font, "PARTY")
//        batch.use {
//            font.color = randomColor(0.3f)
//            font.draw(batch, text, WIDTH/2 - text.width/2,  HEIGHT/2 + text.height/2)
//        }

        tempController()
        val collisionPoint = singleRayCast(playerDir)
        renderer.use(ShapeRenderer.ShapeType.Filled){
            tiles.forEach{ line ->
                line.forEach { tile ->
                    tile.draw(renderer)
                }
            }
            renderer.color = Color.BROWN
            renderer.circle(player.x, player.y, player.radius)
            renderer.color = Color.ORANGE
            renderer.line(player.x, player.y, collisionPoint.x, collisionPoint.y)
        }
    }

    private fun singleRayCast(dir: Vector2): Vector2{
        val rayStepSize = Vector2(
            if (dir.x != 0f) sqrt(1 + (dir.y/dir.x)*(dir.y/dir.x)) else Float.MAX_VALUE,
            if (dir.y != 0f) sqrt(1 + (dir.x/dir.y)*(dir.x/dir.y)) else Float.MAX_VALUE,
        )
        val mapPos = IVector2(
            tileWidth.toInt()*(player.x/tileWidth).toInt(),
            tileHeight.toInt()*(player.y/tileHeight).toInt()
        )
        val rayLengths = Vector2(0f, 0f)
        val step = IVector2(1, 1)

        if (dir.x < 0){
            step.x = -1
            rayLengths.x = (player.x - mapPos.x)*rayStepSize.x
        } else {
            rayLengths.x = (mapPos.x.toFloat() + tileWidth - player.x)*rayStepSize.x
        }

        if (dir.y < 0){
            step.y = -1
            rayLengths.y = (player.y - mapPos.y)*rayStepSize.y
        } else {
            rayLengths.y = (mapPos.y + tileHeight - player.y)*rayStepSize.y
        }

        var hit = false
        var dist = 0f
        while (!hit){
            if (rayLengths.x < rayLengths.y){
                mapPos.x += step.x*tileWidth.toInt()
                dist = rayLengths.x
                rayLengths.x += rayStepSize.x*tileWidth
            }
            else{
                mapPos.y += step.y*tileHeight.toInt()
                dist = rayLengths.y
                rayLengths.y += rayStepSize.y*tileHeight
            }

            if (tiles[mapPos.x/tileWidth.toInt()][mapPos.y/tileHeight.toInt()].color == Color.WHITE) hit = true

        }


        return Vector2(
            player.x + dir.x*dist,
            player.y + dir.y*dist
        )


    }

    private fun rotate(angle: Float): Vector2{
        return Vector2(playerDir.x*cos(angle) - playerDir.y* sin(angle), playerDir.x*sin(angle) + playerDir.y* cos(angle))
    }

    private fun tempController(){
        val speed = 6
        val theta = 2*PI/180
        if (Gdx.input.isKeyPressed(Input.Keys.A)) playerDir = rotate(theta.toFloat())
        if (Gdx.input.isKeyPressed(Input.Keys.D)) playerDir = rotate(-theta.toFloat())

        if (Gdx.input.isKeyPressed(Input.Keys.W)) player.y += playerDir.y*speed
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player.y -= playerDir.y*speed

        var playerRect = Rectangle(player.x - player.radius, player.y - player.radius, 2*player.radius, 2*player.radius)
        tiles.forEach{ line ->
            line.forEach { tile ->
                if (tile.color == Color.WHITE && tile.overlaps(playerRect)){
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
                if (tile.color == Color.WHITE && tile.overlaps(playerRect)){
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