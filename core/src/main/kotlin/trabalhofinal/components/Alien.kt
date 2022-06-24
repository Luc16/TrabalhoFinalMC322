package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import trabalhofinal.components.general.IRayCastTile
import trabalhofinal.components.general.RayCastComponent
import trabalhofinal.utils.AStar
import trabalhofinal.utils.IVector2
import kotlin.random.Random


class Alien(tile: IRayCastTile, val radius: Float, //TODO tirar
            tileWidth: Float, tileHeight: Float,
            texture: Texture,
            color: Color = Color.RED
) : RayCastComponent(texture, tile.i*tileWidth + tileWidth/2, tile.j*tileHeight + tileHeight/2, color,  tile, ComponentType.ALIEN){

    //CONSIDERANDO QUE ALIEN POSSUI 5 DE STAMINA
    fun teleport(path: List<IVector2>?){
        if (path == null) return
        if (path.size < 5){
            this.tile.i = path[path.size-1].i
            this.tile.j = path[path.size-1].j
        } else{
            this.tile.i = path[4].i
            this.tile.j = path[4].j
        }
    }

    fun findClosestPlayer(players: MutableList<Player>, grid: MutableList<MutableList<Tile>>): List<IVector2>?{
        var min = Int.MAX_VALUE
        var closestPath = 0
        val graph = AStar(grid)
        val paths = mutableListOf<List<IVector2>?>()
        for (player in players){
            paths.add(graph.findPath(IVector2(tile.i, tile.j), player.mapPos))
        }
        for (i in 0 until paths.size){
            if (paths[i] != null){
                if (paths[i]!!.size < min){
                    min = paths[i]!!.size
                    closestPath = i
                }
            }
        }
        if (min != Int.MAX_VALUE) return paths[closestPath]
        return null
    }

    fun placeEgg(): Boolean{
        val value = Random.nextInt(1, 100)
        return (value <= 15) //15% de chance de colocar
    }
}