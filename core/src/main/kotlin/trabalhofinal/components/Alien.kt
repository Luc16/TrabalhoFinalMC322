package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.ComponentShip
import trabalhofinal.components.general.ComponentType
import trabalhofinal.components.general.RayCastTile
import trabalhofinal.components.general.RayCastComponent
import trabalhofinal.utils.AStar
import trabalhofinal.utils.IVector2
import trabalhofinal.utils.TextureLoader
import kotlin.random.Random


class Alien(tile: RayCastTile,
            tileWidth: Float, tileHeight: Float,
            texture: Texture,
            mapTexture: Texture
): RayCastComponent(tile, tileWidth, tileHeight, texture, mapTexture) {

    override val type = ComponentType.ALIEN

    private val stamina = 5
    private lateinit var prevTile: RayCastTile

    private fun teleport(destiny: IVector2, ship: ComponentShip){
        prevTile = tile
        tile.component = null
        tile = ship[destiny.i, destiny.j]
        x = tile.i*ship.tileWidth + ship.tileWidth/2
        y = tile.j*ship.tileHeight + ship.tileHeight/2
    }

    private fun findClosestPlayer(ship: ComponentShip, aStar: AStar){
        var min = Int.MAX_VALUE
        var closestPathIdx = 0
        val paths = mutableListOf<List<IVector2>?>()
        val mapPos = IVector2(tile.i, tile.j)

        for (player in ship.players){
            val path = aStar.findPath(mapPos, player.mapPos, true)
            paths.add(path)
        }
        paths.forEachIndexed { i, path ->
            if (path == null) return@forEachIndexed
            if (path.size < min){
                min = path.size
                closestPathIdx = i
            }

        }

        val closest = paths[closestPathIdx]
        if (closest != null && min != Int.MAX_VALUE)
            if (closest.size <= stamina + 1){
                teleport(closest[closest.lastIndex], ship)
                ship.removeComponent(ship.players[closestPathIdx])
            }
            else
                teleport(closest[stamina], ship)
    }

    private fun placeEgg(texture: Texture, mapTexture: Texture, ship: ComponentShip) {
        val value = Random.nextInt(1, 100)
        if (value <= 15) //15% de chance de colocar
            ship.addEgg(Egg(prevTile, ship.tileWidth, ship.tileHeight, texture, mapTexture))
    }

    fun playTurn(textures: TextureLoader, ship: ComponentShip, aStar: AStar){
        findClosestPlayer(ship, aStar)
        placeEgg(textures.egg, textures.eggLogo, ship)
    }
}