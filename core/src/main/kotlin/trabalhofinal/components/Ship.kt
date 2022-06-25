package trabalhofinal.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.components.general.Component
import trabalhofinal.utils.IVector2
import trabalhofinal.utils.MapReader
class Ship(file: String, textures: List<Texture>) {
    val tiles: List<List<Tile>>
    val players = mutableListOf<Player>()
    private val aliens = mutableListOf<Alien>()
    private val fungi = mutableListOf<Fungus>()
    private val eggs = mutableListOf<Fungus>()
    val components = RayCastCompList()
    val sizeI: Int
    var sizeJ: Int
    val tileWidth: Float
    var tileHeight: Float
    var numFungus = 0
    private var fungiToAdd = mutableListOf<Fungus>()

    init {
        val reader = MapReader(file)
        val mapString = reader.contents().reversed()
        sizeJ = mapString[0].length
        sizeI = mapString.size

        tileWidth = WIDTH /sizeJ
        tileHeight = HEIGHT /sizeI

        //cria a matriz de tiles

        val tempTiles = mutableListOf<MutableList<Tile>>()

        for (i in 0 until sizeJ){
            val line = mutableListOf<Tile>()
            for (j in 0 until sizeI){
                val id = mapString[i][j] - '0'
                val color = if (id != 0) Color.WHITE else Color.BLACK
//                val color = when (id){
//                    1 -> Color.WHITE
//                    2 -> Color(0f, 40f/255f, 0f, 1f) // Verde mais escuro
//                    3 -> Color.PURPLE
//                    4 -> Color.WHITE
//                    5 -> Color.YELLOW
//                    6 -> Color.BLUE
//                    7 -> Color.CORAL
//                    8 -> Color.MAGENTA
//                    else -> Color.BLACK
//                }

                val tile = Tile(i, j, tileWidth, tileHeight)
                if (id != 0) {
                    tile.setInitialComponent(
                        if (id == 1) {
                            val fungus = Fungus(tile, textures[5], textures[3])
                            fungi.add(fungus)
                            fungus
                        }
                        else Wall(tile, color, textures[3])
                    )
                }
                line.add(tile)
            }
            tempTiles.add(line)
        }
        tiles = tempTiles.toList()

        val p1 = Pyro(tiles[21][4], 10f, tileWidth, tileHeight,
            Texture(Gdx.files.local("assets/wolftex/pics/alien.png")), color = Color.GREEN
        )
        players.add(p1)
        val p2 = Botanist(tiles[18][4],10f, tileWidth, tileHeight,
            Texture(Gdx.files.local("assets/wolftex/pics/alien.png")),
        )
        players.add(p2)
        aliens.add(Alien(tiles[21][12], tileWidth, tileHeight, Texture(Gdx.files.local("assets/wolftex/pics/alien.png"))))

        // adiciona os componentes
        run {
            components.add(p1)
            components.add(p2)
            components.add(aliens[0])
            components.add(
                Egg(tiles[20][10], tileWidth, tileHeight, Texture(Gdx.files.local("assets/wolftex/pics/barrel-no-bg.png")))
            )

            components.add(
                AlienWeb(tiles[22][2], tileWidth, tileHeight, Texture(Gdx.files.local("assets/wolftex/pics/squareweb.png")))
            )
        }
    }

    operator fun get(i: Int, j: Int) = tiles[i][j]

    fun getFirstPlayer(): Player = players[0]

    fun resetPlayers() = players.forEach { it.reset() }

    fun playAliens(texture: Texture) = aliens.forEach { it.playTurn(texture, this) }

    fun updateComponents(player: Player, zBuffer: List<Float>, tileWidth: Float, tileHeight: Float){
        components.updateComponents(player, zBuffer, tileWidth, tileHeight)
    }

    fun spreadFungus() {
        fungi.forEach { it.spread(this) }
        fungiToAdd.forEach { fungi.add(it) }
        fungiToAdd = mutableListOf()
    }

    fun addFungusLazy(fungus: Fungus) = fungiToAdd.add(fungus)

    fun removeComponent(comp: Component){
        components.remove(comp)
        comp.die()
        when (comp.type) {
            ComponentType.FUNGUS -> fungi.remove(comp)
            ComponentType.EGG -> eggs.remove(comp)
            ComponentType.PLAYER -> players.remove(comp)
            else -> {}
        }

    }

    fun getTilePos(y: Float, x: Float): IVector2 {
        var idx = (x/(tileWidth)).toInt()
        var idy = (y/(tileHeight)).toInt()

        if (idx < 0) idx = 0
        else if (idx >= sizeI) idx = sizeI - 1

        if (idy < 0) idy = 0
        else if (idy >= sizeJ) idy = sizeJ - 1
        return IVector2(idx, idy)
    }



}

