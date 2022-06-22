package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import trabalhofinal.WIDTH
import trabalhofinal.components.general.Component
import trabalhofinal.components.general.IRayCastTile
import trabalhofinal.components.general.RayCastComponent
import trabalhofinal.utils.AStar
import trabalhofinal.utils.IVector2
import java.util.LinkedList
import java.util.Queue
import kotlin.math.*

class Player(x: Float, y: Float,  val radius: Float, //TODO tirar
             texture: Texture,
             tile: IRayCastTile,
             color: Color = Color.LIGHT_GRAY,
): RayCastComponent(texture, x, y, color,  tile, ComponentType.PLAYER) {

    //posicoes tile
    var mapPos = IVector2(tile.i,tile.j)
    val i: Int get() = mapPos.i
    val j: Int get() = mapPos.j

    var isMoving = false

    var destQueue: Queue<IVector2> = LinkedList()

    lateinit var targetComponent: TargetComponent

    var dir = Vector2(1f, 0f)
    var cameraPlane = Vector2(0f, 0.66f)
    private lateinit var dest: IVector2
    fun rotate(angle: Float){
        // rotaciona o jogador utilizando multiplicando a direçao e plano da camera por uma matriz de rotação
        dir = Vector2(dir.x* cos(angle) - dir.y* sin(angle), dir.x* sin(angle) + dir.y* cos(angle))
        cameraPlane = Vector2(cameraPlane.x* cos(angle) - cameraPlane.y* sin(angle), cameraPlane.x* sin(angle) + cameraPlane.y* cos(angle))
    }

    fun calculatePath(dest: IVector2, aStar: AStar){
        val path = aStar.findPath(mapPos, dest)

        if (path != null){
            tile.component = null
            for (pos in path) {
                destQueue.add(pos)
            }
            isMoving = true
        }
        this.dest = destQueue.first()
//        else
//            println("Invalido")
    }

    fun update(tileWidth: Float, tileHeight: Float, tile: IRayCastTile) {
        seen = true
        if (!isMoving || destQueue.isEmpty()) return

        mapPos = IVector2(tile.i, tile.j)
        // TODO arrumar isso
        val speedY = 0.75f*tileWidth / 16
        val speedX = 0.75f*tileHeight / 16

        if (i == dest.i && j == dest.j) {
            if (destQueue.size == 1) {
                x = i*tileWidth + tileWidth/2
                y = j*tileHeight + tileHeight/2
            }
            destQueue.remove(dest)

            if (destQueue.isEmpty()) {
                isMoving = false
                this.tile.component = null
                this.tile = tile
                tile.component = this
            } else {
                dest = destQueue.first()
                val newDir = (dest - mapPos).toVector2()
                cameraPlane = Vector2(newDir.y, newDir.x).scl(0.66f)
                dir = newDir
            }
        } else {
            if (i != dest.i) {
                if (i > dest.i) {
                    x -= speedX
                    return
                } else {
                    x += speedX
                    return
                }
            } else {
                if (j > dest.j) {
                    y -= speedY
                    return
                } else {
                    y += speedY
                    return
                }
            }
        }
    }
}