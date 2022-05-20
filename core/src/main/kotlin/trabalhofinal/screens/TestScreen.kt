package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Rectangle
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.MyGame
import trabalhofinal.WIDTH
import trabalhofinal.components.Tile
import trabalhofinal.utils.MapReader
import kotlin.math.abs
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


    override fun show() {
        val reader = MapReader("assets/test.map")
        val mapString = reader.contents().reversed()
        val width = mapString[0].length
        val height = mapString.size

        val tileWidth = WIDTH/width
        val tileHeight = HEIGHT/height

        for (i in 0 until width){
            val line = mutableListOf<Tile>()
            for (j in 0 until height){
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
        renderer.use(ShapeRenderer.ShapeType.Filled){
            tiles.forEach{ line ->
                line.forEach { tile ->
                    tile.draw(renderer)
                }
            }
            renderer.color = Color.BROWN
            renderer.circle(player.x, player.y, player.radius)
        }
    }

    private fun tempController(){
        val speed = 6
        if (Gdx.input.isKeyPressed(Input.Keys.W)) player.y += speed
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player.y -= speed

        var playerRect = Rectangle(player.x - player.radius, player.y - player.radius, 2*player.radius, 2*player.radius)
        tiles.forEach{ line ->
            line.forEach { tile ->
                if (tile.color == Color.WHITE && tile.overlaps(playerRect)){
                    if (Gdx.input.isKeyPressed(Input.Keys.S))
                        player.y = tile.y + tile.height + player.radius
                    else
                        player.y = tile.y - player.radius
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.x -= speed
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player.x += speed

        playerRect = Rectangle(player.x - player.radius, player.y - player.radius, 2*player.radius, 2*player.radius)

        tiles.forEach{ line ->
            line.forEach { tile ->
                if (tile.color == Color.WHITE && tile.overlaps(playerRect)){
                    if (Gdx.input.isKeyPressed(Input.Keys.A))
                        player.x = tile.x + tile.width + player.radius
                    else
                        player.x = tile.x - player.radius
                }
            }
        }
    }

}