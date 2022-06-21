package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.InputProcessor
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


class GridScreen(game: MyGame): CustomScreen(game), InputProcessor {
    private val grid = mutableListOf<MutableList<Tile>>()
    private var mapWidth = 0
    private var mapHeight = 0
    private var tileWidth = 0f
    private var tileHeight = 0f
    private val players = mutableListOf<Player>()
    private var activePlayer: Player? = null
    // path

    init {
        Gdx.input.inputProcessor = this

        val reader = MapReader("assets/maps/test.map")
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

        val pl = Player((grid.size - (22 + 1))*tileWidth + tileWidth/2, 1*tileHeight + tileHeight/2, 10f)
        val pl2 = Player((grid.size - (21 + 1))*tileWidth + tileWidth/2, 1*tileHeight + tileHeight/2, 10f)
        pl.pos = IVector2(22,1)
        pl2.pos = IVector2(21,1)
        players.add(pl)
        players.add(pl2)
        activePlayer = players[0]
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Keys.Q)) Gdx.app.exit()
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) mouseController(activePlayer) //TODO: implementar qual o player atual
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
            renderer.color = Color.BLUE
            renderer.circle(players[1].x, players[1].y, 10f)
        }
    }

    private fun getTilePos(y: Float, x: Float): IVector2 {
        var idx = (x/tileWidth).toInt()
        var idy = (y/tileHeight).toInt()

        if (idx < 0) idx = 0
        else if (idx >= grid.size) idx = grid.size

        if (idy < 0) idy = 0
        else if (idy >= grid[0].size) idy = grid.size
        return IVector2(idx, idy)
    }

    private fun mouseController(activePlayer: Player?) {
        if (activePlayer == null || activePlayer.isMoving) return

        val xPos = WIDTH - Gdx.input.x
        val yPos = HEIGHT - Gdx.input.y

        val dest = getTilePos(yPos, xPos)
        val currTile = grid[dest.i][dest.j]

        if (currTile.component == null){
                val graph = AStar(grid)
                val src = activePlayer.pos
                val path = graph.findPath(src, dest)

                if (path != null){
                    grid[activePlayer.i][activePlayer.j].component = null
                    for (pos in path) {
                        //grid[pos.i][pos.j].component?.color = Color.RED
                        activePlayer.destQueue.add(pos)
                    }
                    activePlayer.isMoving = true
                } else
                    println("Invalido")

        } else if (currTile.component!!.type == ComponentType.PLAYER){
            changePlayer(currTile.component as Player)
            println("trocando jogador")
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
    private fun changePlayer(player: Player){
        activePlayer?.let { it.color = Color.LIGHT_GRAY }
        activePlayer = player
        player.color = Color.RED
    }

    private fun update(player: Player){
        if (!player.isMoving || player.destQueue.isEmpty()){
            grid[player.pos.i][player.pos.j].component = player
            return
        }

        val dest = player.destQueue.first()

        val dir = dest - player.pos
        player.pos = getTilePos(player.y - dir.j*tileHeight/2, WIDTH - player.x - dir.i*tileWidth/2)
        val speedY = tileWidth/16
        val speedX = tileHeight/16

        if (player.i == dest.i && player.j == dest.j){
            if (player.destQueue.size == 1){
                player.x = WIDTH - player.i*tileWidth - tileWidth/2
                player.y = player.j*tileWidth + tileWidth/2
            }
            player.destQueue.remove(dest)
            if (player.destQueue.isEmpty()) player.isMoving = false
        } else{
            if (player.i != dest.i){
                if (player.i > dest.i){
                    player.x += speedY
                    return
                } else{
                    player.x -= speedY
                    return
                }
            } else{
                if (player.j > dest.j){
                    player.y -= speedX
                    return
                } else{
                    player.y += speedX
                    return
                }
            }
        }
    }


    override fun keyDown(keycode: Int): Boolean = true
    override fun keyUp(keycode: Int): Boolean = true
    override fun keyTyped(character: Char): Boolean = true
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = true
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean = true
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean = true
    override fun mouseMoved(screenX: Int, screenY: Int): Boolean = true
    override fun scrolled(amountX: Float, amountY: Float): Boolean = true
}