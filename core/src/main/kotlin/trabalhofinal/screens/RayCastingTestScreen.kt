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
    private val alien = Component(Texture(Gdx.files.local("assets/wolftex/pics/alien.png")), Vector2())
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
    private val raycastIsMinimap = false


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
                    Tile(i, j, tileWidth, tileHeight, color,
                        if (id != 0) textures[id-1] else null, id)
                )
            }
            tiles.add(line)
        }

        alien.tile = tiles[12][21]
        alien.pos.set(tileWidth*12 + tileWidth/2, HEIGHT - tileHeight*3 + tileHeight/2)

        rayCaster = RayCaster(tiles, tileWidth, tileHeight, shader)

        Gdx.input.isCursorCatched = true
        Gdx.gl.glEnable(GL20.GL_BLEND)
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Keys.Q)) Gdx.app.exit()

        tempController()
        rayCaster.multipleRayCast3D(player)
        clearScreen(1f, 1f, 1f, 1f)

        rayCaster.meshes.render(viewport.camera)
        rayCaster.meshes.dispose()

        alien.render(player, rayCaster.zBuffer, shader, tileWidth, tileHeight)
        alien.tile.color = if (alien.seen) Color.BROWN else Color.BLACK

        // minimap
        drawTileMinimap(0.2f)


    }

    private fun drawTileMinimap(ratio: Float){
        val minimapRect = Rectangle(5f, HEIGHT - HEIGHT*ratio - 5f, WIDTH*ratio, HEIGHT*ratio)
        val mirroredX = minimapRect.x + minimapRect.width
        renderer.use(ShapeRenderer.ShapeType.Filled, viewport.camera.combined){
            renderer.color = Color.BLACK
            renderer.rect(minimapRect.x, minimapRect.y, minimapRect.width, minimapRect.height)
            renderer.color = Color.LIGHT_GRAY
            rayCaster.collisionPoints.forEach{
                renderer.rectLine(
                    mirroredX - player.x*ratio,
                    minimapRect.y + player.y*ratio,
                    mirroredX - it.x*ratio,
                    minimapRect.y + it.y*ratio, 1f)
            }
            tiles.forEach{ line ->
                line.forEach { tile ->
                    if (tile.color != Color.BLACK){
                        renderer.color = tile.color
                        renderer.rect(
                            mirroredX - tileWidth*ratio - tile.x*ratio,
                            minimapRect.y + tile.y*ratio,
                            tile.width*ratio, tile.height*ratio)
                    }
                }
            }
            renderer.color = Color.BROWN
            renderer.circle(mirroredX - player.x*ratio, minimapRect.y + player.y*ratio, player.radius*ratio)
        }
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
                if (tile.id != 0 && tile.r.overlaps(playerRect)){
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
                if (tile.id != 0 && tile.r.overlaps(playerRect)){
                    if (Gdx.input.isKeyPressed(Keys.S) && player.dir.x > 0 ||
                        Gdx.input.isKeyPressed(Keys.W) && player.dir.x < 0)
                        player.x = tile.x + tile.width + player.radius
                    else
                        player.x = tile.x - player.radius
                }
            }
        }
    }

    override fun dispose() {
        shader.dispose()
    }

}