package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.MyGame
import trabalhofinal.WIDTH
import trabalhofinal.components.Player
import trabalhofinal.components.Tile
import trabalhofinal.utils.MapReader
import trabalhofinal.utils.RayCaster
import kotlin.math.*

class RayCastingTestScreen(game: MyGame): CustomScreen(game) {

    private val tiles = mutableListOf<MutableList<Tile>>()

    private lateinit var rayCaster: RayCaster
    private val player = Player(WIDTH/2, HEIGHT/2, 10f)
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

        rayCaster = RayCaster(tiles, tileWidth, tileHeight)

    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) Gdx.app.exit()
        tempController()
        val collisionWallsColors = rayCaster.multipleRayCast3D(player)
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


    private fun tempController(){
        val speed = 4
        val theta = 2*PI/180

        val deltaX = Gdx.input.deltaX.toFloat()/10
        player.rotate(deltaX*theta.toFloat())
        Gdx.input.setCursorPosition((WIDTH/2).toInt(), (HEIGHT/2).toInt())

        if (Gdx.input.isKeyPressed(Input.Keys.W)) player.y += player.dir.y*speed
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player.y -= player.dir.y*speed

        var playerRect = Rectangle(player.x - player.radius, player.y - player.radius, 2*player.radius, 2*player.radius)
        tiles.forEach{ line ->
            line.forEach { tile ->
                if (tile.color != Color.BLACK && tile.overlaps(playerRect)){
                    if (Gdx.input.isKeyPressed(Input.Keys.S) && player.dir.y > 0 ||
                        Gdx.input.isKeyPressed(Input.Keys.W) && player.dir.y < 0)
                        player.y = tile.y + tile.height + player.radius
                    else
                        player.y = tile.y - player.radius
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)) player.x += player.dir.x*speed
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player.x -= player.dir.x*speed

        playerRect = Rectangle(player.x - player.radius, player.y - player.radius, 2*player.radius, 2*player.radius)

        tiles.forEach{ line ->
            line.forEach { tile ->
                if (tile.color != Color.BLACK && tile.overlaps(playerRect)){
                    if (Gdx.input.isKeyPressed(Input.Keys.S) && player.dir.x > 0 ||
                        Gdx.input.isKeyPressed(Input.Keys.W) && player.dir.x < 0)
                        player.x = tile.x + tile.width + player.radius
                    else
                        player.x = tile.x - player.radius
                }
            }
        }
    }

}