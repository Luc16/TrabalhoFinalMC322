package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import ktx.app.clearScreen
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.MyGame
import trabalhofinal.WIDTH
import trabalhofinal.components.Component
import trabalhofinal.components.Player
import trabalhofinal.components.Tile
import trabalhofinal.utils.MapReader
import trabalhofinal.utils.RayCaster
import trabalhofinal.utils.graphics.fragmentShader
import trabalhofinal.utils.graphics.vertexShader
import kotlin.math.PI


class RayCastingTestScreen(game: MyGame): CustomScreen(game) {

    private val tiles = mutableListOf<MutableList<Tile>>()
    private val shader = ShaderProgram(vertexShader, fragmentShader)


    private lateinit var rayCaster: RayCaster
    private val player = Player(175.62909f,696.82367f, 10f)
    private var mapWidth = 0
    private var mapHeight = 0
    private var tileWidth = 0f
    private var tileHeight = 0f
    private val barrel = Component(Texture(Gdx.files.local("assets/wolftex/pics/alien.png")), Vector2())
    private val textures = listOf(
        Texture(Gdx.files.local("assets/wolftex/pics/eagle.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/redbrick.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/purplestone.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/greystone.png")),
        Texture(Gdx.files.local("assets/wolftex/pics/trig.png")),
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

        barrel.tile = tiles[12][21]
        barrel.pos.set(tileWidth*12 + tileWidth/2, HEIGHT - tileHeight*3 + tileHeight/2)

        rayCaster = RayCaster(tiles, tileWidth, tileHeight, shader)

        Gdx.input.isCursorCatched = true
        Gdx.gl.glEnable(GL20.GL_BLEND)
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Keys.Q)) Gdx.app.exit()

        tempController()
        val (collisionPoints, meshesToDraw, zBuffer) = rayCaster.multipleRayCast3D(player)
        clearScreen(1f, 1f, 1f, 1f)

        meshesToDraw.render(viewport.camera)
        meshesToDraw.dispose()

        barrel.render(player, zBuffer, shader, tileWidth, tileHeight)
        barrel.tile.color = if (barrel.seen) Color.BROWN else Color.BLACK

        // minimap
        drawTileMinimap(0.2f)


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

        if (Gdx.input.isKeyPressed(Keys.W)) player.y += player.dir.y*speed
        if (Gdx.input.isKeyPressed(Keys.S)) player.y -= player.dir.y*speed

        var playerRect = Rectangle(player.x - player.radius, player.y - player.radius, 2*player.radius, 2*player.radius)
        tiles.forEach{ line ->
            line.forEach { tile ->
                if (tile.id != 0 && tile.overlaps(playerRect)){
                    if (Gdx.input.isKeyPressed(Keys.S) && player.dir.y > 0 ||
                        Gdx.input.isKeyPressed(Keys.W) && player.dir.y < 0)
                        player.y = tile.y + tile.height + player.radius
                    else
                        player.y = tile.y - player.radius
                }
            }
        }

        if (Gdx.input.isKeyPressed(Keys.W)) player.x += player.dir.x*speed
        if (Gdx.input.isKeyPressed(Keys.S)) player.x -= player.dir.x*speed

        playerRect = Rectangle(player.x - player.radius, player.y - player.radius, 2*player.radius, 2*player.radius)

        tiles.forEach{ line ->
            line.forEach { tile ->
                if (tile.id != 0 && tile.overlaps(playerRect)){
                    if (Gdx.input.isKeyPressed(Keys.S) && player.dir.x > 0 ||
                        Gdx.input.isKeyPressed(Keys.W) && player.dir.x < 0)
                        player.x = tile.x + tile.width + player.radius
                    else
                        player.x = tile.x - player.radius
                }
            }
        }
    }

}