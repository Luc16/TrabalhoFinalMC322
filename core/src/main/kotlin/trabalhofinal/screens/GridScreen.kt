package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import ktx.app.clearScreen
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.MyGame
import trabalhofinal.WIDTH
import trabalhofinal.components.ComponentType
import trabalhofinal.components.Player
import trabalhofinal.components.Tile
import trabalhofinal.components.Wall
import trabalhofinal.utils.IVector2
import trabalhofinal.utils.MapReader
import trabalhofinal.utils.AStar
import kotlin.math.floor
import kotlin.math.roundToInt


class GridScreen(game: MyGame): CustomScreen(game) {
    private val grid = mutableListOf<MutableList<Tile>>()
    private var mapWidth = 0
    private var mapHeight = 0
    private var tileWidth = 0f
    private var tileHeight = 0f
    private val players = mutableListOf<Player>()
    // path

    override fun show() {
        val reader = MapReader("assets/test.map")
        val mapString = reader.contents().reversed()
        mapWidth = mapString[0].length
        mapHeight = mapString.size

        tileWidth = WIDTH /mapWidth
        tileHeight = HEIGHT /mapHeight

        for (i in 0 until mapWidth){
            val line = mutableListOf<Tile>()
            for (j in 0 until mapHeight){
                val id = mapString[i][j] - '0'
                val color = when (id){
                    1 -> Color.RED
                    2 -> Color(0f, 40f/255f, 0f, 1f) // Verde mais escuro
                    3 -> Color.PURPLE
                    4 -> Color.WHITE
                    5 -> Color.YELLOW
                    6 -> Color.BLUE
                    7 -> Color.CORAL
                    8 -> Color.MAGENTA
                    else -> Color.WHITE
                }
                line.add(
                    Tile(i, j, tileWidth, tileHeight, if(id == 0) null else Wall(color, null))
                )
            }
            grid.add(line)
        }

        val pl = Player(1.5f*tileWidth, 1.5f*tileHeight, 10f)
        pl.pos = IVector2(22,1)
        players.add(pl)
        Gdx.gl.glEnable(GL20.GL_BLEND)
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) Gdx.app.exit()
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) mouseController(players[0]) //TODO: implementar qual o player atual
        clearScreen(1f, 1f, 1f, 1f)

        players.forEach {
            update(it) //TODO update eh metodo do jogador
        }

        renderer.use(ShapeRenderer.ShapeType.Filled, viewport.camera.combined){
            renderer.color = Color.BLACK
            renderer.rect(0f, 0f, WIDTH, HEIGHT)
            renderer.color = Color.LIGHT_GRAY
            grid.forEach{ line ->
                line.forEach { tile ->
                    tile.draw(WIDTH, 0f, 1f, renderer)
                }
            }
            renderer.color = Color.GOLD
            renderer.circle(players[0].x, players[0].y, 10f)
        }
    }

    private fun getTilePos(x: Float, y: Float): IVector2? {
        val idx = (floor(x/tileWidth)).roundToInt()
        val idy = (floor(y/tileHeight)).roundToInt()
        if (idx < 0 || idy < 0 || idy >= grid[0].size || idx >= grid.size) return null //coordenada invalida
        return IVector2(idy, idx)
    }

    private fun mouseController(currPlayer: Player) {
        val xPos = WIDTH - Gdx.input.x
        val yPos = HEIGHT - Gdx.input.y

        val d1 = getTilePos(xPos, yPos) ?: return
        val dest = IVector2(d1.j, d1.i)
        val currTile = grid[dest.i][dest.j]

        if (currTile.component == null){
                val graph = AStar(grid)
                val src = currPlayer.pos
                val path = graph.findPath(src, dest)

                if (path != null){
                    for (pos in path) {
                        grid[pos.i][pos.j].component?.color = Color.RED
                        currPlayer.destQueue.add(pos)
                    }
                    currPlayer.isMoving = true
                } else
                    println("Invalido")

        }
//        else{
//            when(currTile.component!!.type){
//                //TODO: implementar clique em jogador -> trocar jogador
//                ComponentType.PLAYER -> TODO()
//                ComponentType.WALL -> TODO()
//                ComponentType.DOOR -> TODO()
//                ComponentType.EGG -> TODO()
//                ComponentType.ALIEN -> TODO()
//                ComponentType.FUNGUS -> TODO()
//            }
//        }
    }

    private fun update(player: Player){
        if (!player.isMoving || player.destQueue.isEmpty()) return
        val dest = player.destQueue.first()

        val p = getTilePos(player.y, WIDTH - player.x)
        val pos = p ?: IVector2(0, 0)
        val speedY = tileWidth
        val speedX = tileHeight

        if (pos.i == dest.i && pos.j == dest.j){
            player.pos.i = dest.i
            player.pos.j = dest.j
            player.destQueue.remove(dest)
            if (player.destQueue.isEmpty()) player.isMoving = false
        } else{
            if (pos.i != dest.i){
                if (pos.i > dest.i){
                    player.x += speedY
                    return
                } else{
                    player.x -= speedY
                    return
                }
            } else{
                if (pos.j > dest.j){
                    player.y -= speedX
                    return
                } else{
                    player.y += speedX
                    return
                }
            }
        }
    }
}