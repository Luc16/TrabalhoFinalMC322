package trabalhofinal.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import trabalhofinal.HEIGHT
import trabalhofinal.WIDTH
import trabalhofinal.components.general.*
import trabalhofinal.utils.AStar
import trabalhofinal.utils.IVector2
import trabalhofinal.utils.MapReader
import trabalhofinal.utils.TextureLoader
import kotlin.math.max

class Ship(file: String, textures: TextureLoader): ComponentShip {
    val tiles: List<List<Tile>>
    override val drawableTiles: List<List<DrawableTile>>
        get() = tiles
    override val players = mutableListOf<Player>()
    private val aliens = mutableListOf<Alien>()
    private val fungi = mutableListOf<Fungus>()
    private val eggs = mutableListOf<Egg>()
    override val components = RayCastCompList()
    val sizeI: Int
    var sizeJ: Int
    override val tileWidth: Float
    override val tileHeight: Float
    override var numFungi = 0
    private var fungiToAdd = mutableListOf<Fungus>()
    override val numEggs get() = eggs.size
    val numPlayers get() = players.size
    override val maxFungi: Int

    init {
        val reader = MapReader(file)
        val mapString = reader.contents()

        maxFungi = mapString[mapString.lastIndex].toInt()
        sizeJ = mapString[0].length
        sizeI = mapString.size - 1

        tileWidth = WIDTH /sizeJ
        tileHeight = HEIGHT/sizeI

        val tempTiles = mutableListOf<MutableList<Tile>>()

        for (i in 0 until sizeJ){
            val line = mutableListOf<Tile>()
            for (j in 0 until sizeI){
                val id = mapString[i][j] - '0'
                val tile = Tile(i, j, tileWidth, tileHeight)

                tile.setInitialComponent(
                    when (id){
                        1 -> Wall(tile, textures.wall)
                        2 -> {
                            val fungus = Fungus(tile, textures.fungus, textures.wall)
                            fungi.add(fungus)
                            numFungi++
                            fungus
                        }
                        3 -> {
                            val web = AlienWeb(tile, tileWidth, tileHeight, textures.web, textures.egg_logo)
                            components.add(web)
                            web
                        }
                        4 -> {
                            val p = Pyro(tile, tileWidth, tileHeight, textures.pyro, textures.pyroLogo)
                            components.add(p)
                            players.add(p)
                            p
                        }
                        5 -> {
                            val p = Recon(tile, tileWidth, tileHeight, textures.recon, textures.reconLogo)
                            components.add(p)
                            players.add(p)
                            p
                        }
                        6 -> {
                            val p = Botanist(tile, tileWidth, tileHeight, textures.botanist, textures.botanistLogo)
                            components.add(p)
                            players.add(p)
                            p
                        }
                        7 -> {
                            val egg = Egg(tile, tileWidth, tileHeight, textures.egg, textures.egg_logo)
                            addEgg(egg)
                            egg
                        }
                        8 -> {
                            val alien = Alien(tile, tileWidth, tileHeight, textures.alien, textures.egg_logo)
                            aliens.add(alien)
                            components.add(alien)
                            alien
                        }
                        else -> null
                    }
                )
                line.add(tile)
            }
            tempTiles.add(line)
        }
        tiles = tempTiles.toList()
    }

    override operator fun get(i: Int, j: Int) = tiles[i][j]

    fun getFirstPlayer(): Player {

        players[0].isSelected = true
        return players[0]
    }

    fun resetPlayers() = players.forEach { it.reset() }

    fun playAliens(textures: TextureLoader, aStar: AStar) = aliens.forEach { it.playTurn(textures, this, aStar) }

    fun updateComponents(player: Player, zBuffer: List<Float>, tileWidth: Float, tileHeight: Float){
        components.updateComponents(player, zBuffer, tileWidth, tileHeight)
    }

    fun spreadFungus() {
        fungi.forEach { it.spread(this) }
        fungiToAdd.forEach { fungi.add(it) }
        fungiToAdd = mutableListOf()
    }

    fun addFungusLazy(fungus: Fungus) = fungiToAdd.add(fungus)

    override fun addEgg(egg: Egg){
        components.add(egg)
        eggs.add(egg)
    }

    override fun renderComponents(shader: ShaderProgram, initialX: Float, initialY: Float, ratio: Float) = components.render(shader, initialX, initialY, ratio)

    override fun removeComponent(comp: Component){
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

