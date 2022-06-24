package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.utils.IVector2
import trabalhofinal.utils.MapReader

class Ship(file: String, textures: List<Texture>) {
    var tiles = listOf(listOf<Tile>())
    private set
    var sizeI = 0
    private set
    var sizeJ = 0
    private set
    var tileWidth = 0f
    private set
    var tileHeight = 0f
    private set

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
                    Tile(i, j, tileWidth, tileHeight, if (id == 0) null else Wall(color, textures[id-1]))
                )
            }
            tempTiles.add(line)
        }
        tiles = tempTiles.toList()
    }

    operator fun get(i: Int, j: Int) = tiles[i][j]

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