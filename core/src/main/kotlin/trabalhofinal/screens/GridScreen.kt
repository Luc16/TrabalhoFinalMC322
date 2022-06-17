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

    init{
        val pl = Player(20f, 20f, 10f)
        pl.pos = IVector2(1,1)
        players.add(pl)
    }

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
        Gdx.gl.glEnable(GL20.GL_BLEND)
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) Gdx.app.exit()

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) mouseController(players[0]) //TODO: implementar qual o player atual
        clearScreen(1f, 1f, 1f, 1f)

        // curPlayer.update()

        renderer.use(ShapeRenderer.ShapeType.Filled, viewport.camera.combined){
            renderer.color = Color.BLACK
            renderer.rect(0f, 0f, WIDTH, HEIGHT)
            renderer.color = Color.LIGHT_GRAY
            grid.forEach{ line ->
                line.forEach { tile ->
                    tile.draw(0f, 0f, 1f, renderer)
                }
            }
            renderer.circle(players[0].pos.i*(tileWidth) + 0.5f*tileWidth, players[0].pos.j*(tileHeight) + 0.5f*tileHeight, 10f)
        }
    }

    private fun getTilePos(x: Int, y: Int): IVector2? {
        val idx = (floor(x/tileWidth)).roundToInt()
        val idy = (floor(y/tileHeight)).roundToInt()
        if (idx < 0 || idy < 0 || idy >= grid[0].size || idx >= grid.size) return null //coordenada invalida
        return IVector2(idx, idy)
    }

    private fun mouseController(currPlayer: Player) {
        val xPos = Gdx.input.x
        val yPos = HEIGHT.toInt() - Gdx.input.y

        //codigo para movimento, depois vai ter que separar uma interacao especifica pra cada tipo de tile
        // provavelmente so colocar um when pra separar caso por caso
        val dest = getTilePos(xPos, yPos)

        // if (!p.moving) path++ ; moveDnv
//        fun setDest(i, j)
//        fun move(){
//            if (p.pos != dest){
//                p.y += movY
//                p.x += movX
//            }
//        }

        // quero mexer 1f na direcao certa a cada 0.05s

        if (dest != null){
            val currTile = grid[dest.i][dest.j]
            when(currTile.component){
                null ->{
                    val graph = AStar(grid)
                    val src = currPlayer.pos
                    val path = graph.findPath(src, dest)

                    if (path != null){
                        for (pos in path) {

                            //curPlayer.update()
                            currPlayer.move(pos.i, pos.j)
                        }
                    } else {
                        println("Invalido")
                    }
                }
                //TODO: implementar clique em jogador -> trocar jogador (?)
            }
        }
    }
}