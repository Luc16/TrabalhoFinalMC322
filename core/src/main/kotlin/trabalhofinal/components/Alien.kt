package trabalhofinal.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.IMapDrawable
import trabalhofinal.components.general.IRayCastTile
import trabalhofinal.components.general.RayCastComponent
import trabalhofinal.utils.AStar
import trabalhofinal.utils.IVector2
import kotlin.random.Random


class Alien(tile: IRayCastTile,
            tileWidth: Float, tileHeight: Float,
            texture: Texture,
            color: Color = Color.BROWN,
): RayCastComponent(tile, tileWidth, tileHeight, texture, color) {

    override val type = ComponentType.ALIEN

    private val stamina = 5

    private fun teleport(destiny: IVector2, ship: Ship){
        if (tile.component?.type == ComponentType.ALIEN) tile.component = null
        tile = ship[destiny.i, destiny.j]
        x = tile.i*ship.tileWidth + ship.tileWidth/2
        y = tile.j*ship.tileHeight + ship.tileHeight/2
    }

    private fun findClosestPlayer(ship: Ship){
        var min = Int.MAX_VALUE
        var closestPath = 0
        val graph = AStar(ship.tiles)
        val paths = mutableListOf<List<IVector2>?>()
        for (player in ship.players){
            paths.add(graph.findPath(IVector2(tile.i, tile.j), player.mapPos, true))
        }
        for (i in 0 until paths.size){
            if (paths[i] != null){
                if (paths[i]!!.size < min){
                    min = paths[i]!!.size
                    closestPath = i
                }
            }
        }
        val closest = paths[closestPath]
        if (closest != null && min != Int.MAX_VALUE)
            if (closest.size < stamina + 1)
                teleport(closest[closest.lastIndex], ship)
            else
                teleport(closest[stamina], ship)
    }

    private fun placeEgg(texture: Texture, ship: Ship) {
        val value = Random.nextInt(1, 100)
        if (value <= 15) //15% de chance de colocar
            ship.components.add(Egg(tile, ship.tileWidth, ship.tileHeight, texture))
    }

    fun playTurn(texture: Texture, ship: Ship){
        placeEgg(texture, ship)
        findClosestPlayer(ship)
    }
}