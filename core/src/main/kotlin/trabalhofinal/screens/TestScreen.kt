package trabalhofinal.screens

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.MyGame
import trabalhofinal.WIDTH
import trabalhofinal.components.Tile
import trabalhofinal.utils.MapReader
import kotlin.random.Random

fun randomColor(offset: Float) = Color(
    offset + Random.nextFloat(),
    offset + Random.nextFloat(),
    offset + Random.nextFloat(), 1f
)

class TestScreen(game: MyGame): CustomScreen(game) {

    private val tiles = mutableListOf<MutableList<Tile>>()
    private val text = GlyphLayout()

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
        renderer.use(ShapeRenderer.ShapeType.Filled){
            tiles.forEach{ line ->
                line.forEach { tile ->
                    tile.draw(renderer)
                }
            }
        }
    }

}