package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
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
import trabalhofinal.utils.graphics.fragmentShader
import trabalhofinal.utils.graphics.vertexShader
import kotlin.math.*

class RayCastingTestScreen(game: MyGame): CustomScreen(game) {

    private val tiles = mutableListOf<MutableList<Tile>>()
    private val shader = ShaderProgram(vertexShader, fragmentShader)


    private lateinit var rayCaster: RayCaster
    private val player = Player(WIDTH/2, HEIGHT/2, 10f)
    private var mapWidth = 0
    private var mapHeight = 0
    private var tileWidth = 0f
    private var tileHeight = 0f
    private val textures = listOf(
        Texture(Gdx.files.local("assets/wolftex/pics/eagle.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/redbrick.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/purplestone.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/greystone.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/bluestone.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/mossy.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/wood.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/colorstone.png")),
    )


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
                val id = mapString[j][i] - '0'
                val color = when (id){
                    1 -> Color.RED
                    2 -> Color(0f, 40f/255f, 0f, 1f) // Verde mais escuro
                    3 -> Color.PURPLE
                    4 -> Color.WHITE
                    5 -> Color.YELLOW
                    6 -> Color.BLUE
                    7 -> Color.CORAL
                    8 -> Color.MAGENTA
                    else -> Color.BLACK
                }
                line.add(
                    Tile(i*tileWidth, j*tileHeight, tileWidth, tileHeight, color,
                        if (id != 0) textures[id-1] else null, id)
                )
            }
            tiles.add(line)
        }

        Gdx.input.isCursorCatched = true

        rayCaster = RayCaster(tiles, tileWidth, tileHeight, shader)

    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) Gdx.app.exit()
        tempController()
        val (collisionPoints, quadsToDraw) = rayCaster.multipleRayCast3D(player)

        quadsToDraw.render(viewport.camera)
        quadsToDraw.dispose()

        // minimap
        renderer.use(ShapeRenderer.ShapeType.Filled, viewport.camera.combined){
            renderer.color = Color.BLACK
            renderer.rect(5f, HEIGHT - 165f, 160f, 160f)
            renderer.color = Color.LIGHT_GRAY
            collisionPoints.forEach{
                renderer.rectLine(165f - player.x/5, HEIGHT - 165f + player.y/5, 165f - it.x/5, HEIGHT - 165f + it.y/5, 1f)
            }
            tiles.forEach{ line ->
                line.forEach { tile ->
                    if (tile.id != 0){
                        renderer.color = tile.color
                        renderer.rect(165f - tileWidth/5 - tile.x/5, HEIGHT - 165f + tile.y/5, tile.width/5, tile.height/5)
                    }
                }
            }
            renderer.color = Color.BROWN
            renderer.circle(165f - player.x/5, HEIGHT - 165f + player.y/5, player.radius/5)
        }
    }

    override fun dispose() {
        shader.dispose()
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
                if (tile.id != 0 && tile.overlaps(playerRect)){
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
                if (tile.id != 0 && tile.overlaps(playerRect)){
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