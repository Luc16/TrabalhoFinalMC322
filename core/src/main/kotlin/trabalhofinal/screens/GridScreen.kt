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
import trabalhofinal.components.Tile
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
                    else -> Color.BLACK
                }
                line.add(
                    Tile(i, j, tileWidth, tileHeight, color, null, id)
                )
            }
            grid.add(line)
        }
        Gdx.gl.glEnable(GL20.GL_BLEND)
    }

    override fun render(delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) Gdx.app.exit()

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) mouseController()
        clearScreen(1f, 1f, 1f, 1f)

        renderer.use(ShapeRenderer.ShapeType.Filled, viewport.camera.combined){
            renderer.color = Color.BLACK
            renderer.rect(0f, 0f, WIDTH, HEIGHT)
            renderer.color = Color.LIGHT_GRAY
            grid.forEach{ line ->
                line.forEach { tile ->
                    if (tile.color != Color.BLACK){
                        renderer.color = tile.color
                        renderer.rect(tile.x, tile.y, tile.width - 1f, tile.height -1f)
                    }
                }
            }
        }

    }

    private fun getTilePos(x: Int, y: Int): IVector2? {
        val idx = (floor(x/tileWidth)).roundToInt()
        val idy = (floor(y/tileHeight)).roundToInt()
        if (idx < 0 || idy < 0 || idy >= grid[0].size || idx >= grid.size) return null //coordenada invalida
        return IVector2(idx, idy)
    }

    //colocar reconhecimento de coordenada
    private fun mouseController() {
        val xPos = Gdx.input.x
        val yPos = HEIGHT.toInt() - Gdx.input.y

        //codigo para movimento, depois vai ter que separar uma interacao especifica pra cada tipo de tile
        // provavelmente so colocar um when pra separar caso por caso
        val dest = getTilePos(xPos, yPos)
        if (dest != null){
            val currTile = grid[dest.i][dest.j]
            currTile.color = Color.RED
            when(currTile.id){
                0 ->{
                    val graph = AStar(grid)
                    val test = IVector2(1,1)
                    val path = graph.findPath(test, dest)

                    if (path != null){
                        for (pos in path){
                            grid[pos.i][pos.j].color = Color.RED
                        }
                    } else {
                        println("Invalido")
                    }
                }
            }
        }
    }
}