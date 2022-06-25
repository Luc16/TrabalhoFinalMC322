package trabalhofinal.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import trabalhofinal.components.general.IMapDrawable
import trabalhofinal.components.general.IRayCastTile
import trabalhofinal.components.general.RayCastComponent
import trabalhofinal.utils.AStar
import trabalhofinal.utils.IVector2
import java.util.LinkedList
import java.util.Queue
import kotlin.math.*

abstract class Player(tile: IRayCastTile, private val radius: Float, //TODO tirar
                      tileWidth: Float, tileHeight: Float,
                      texture: Texture,
                      color: Color = Color.WHITE,
):  IMapDrawable,
    RayCastComponent(tile, tileWidth, tileHeight, texture, color) {

    override val type = ComponentType.PLAYER
    abstract val name: String

    //posicoes tile
    var mapPos = IVector2(tile.i,tile.j)
    val i: Int get() = mapPos.i
    val j: Int get() = mapPos.j

    var energy = 5
    var isMoving = false

    abstract val maxEnergy: Int
    abstract val webEnergy: Int
    abstract val fungusEnergy: Int
    abstract val eggEnergy: Int


    private var destQueue: Queue<IVector2> = LinkedList()
    private var pathLen = 0

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
            for (k in 0 until min(energy + 1, path.size))
                destQueue.add(path[k])

            pathLen = destQueue.size
            isMoving = true
        }
        this.dest = destQueue.first()
    }

    override fun update(player: Player, zBuffer: List<Float>, tileWidth: Float, tileHeight: Float) {
        super.update(player, zBuffer, tileWidth, tileHeight)
        seen = true
    }

    fun updateSelected(tileWidth: Float, tileHeight: Float, mapRatio: Float, tile: IRayCastTile) {
        if (!isMoving || destQueue.isEmpty()) return
        mapPos = IVector2(tile.i, tile.j)

        val speedY = mapRatio*tileWidth / 16
        val speedX = mapRatio*tileHeight / 16

        if (i == dest.i && j == dest.j) {
            if (destQueue.size == 1) {
                x = i*tileWidth + tileWidth/2
                y = j*tileHeight + tileHeight/2
            }
            if (pathLen > destQueue.size) {
                energy--
            }
            destQueue.remove(dest)

            if (destQueue.isEmpty()) {
                isMoving = false
                this.tile.component = null
                this.tile = tile
            } else {
                dest = destQueue.first()
                val newDir = (dest - mapPos).toVector2()
                cameraPlane = Vector2(newDir.y, newDir.x).scl(if (abs(newDir.y) > 0f) -0.66f else 0.66f)
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

    fun reset(){
        energy = maxEnergy
    }

    override fun draw(startX: Float, startY: Float, ratio: Float, renderer: ShapeRenderer) {
        renderer.circle(startX - x * ratio, startY + y * ratio, radius * ratio)
    }

    fun interact(ship: Ship){
        when(targetComponent.type){
            ComponentType.WEB -> {
                if (energy < webEnergy) return
                targetComponent.color = Color.BLACK
                targetComponent.die()
                ship.removeComponent(targetComponent.component)
                energy -= webEnergy
            }
            ComponentType.FUNGUS -> {
                if (energy < fungusEnergy) return
                targetComponent.color = Color.WHITE //TODO ver cor certa da parede
                targetComponent.die()
                ship.removeComponent(targetComponent.component)
                energy -= fungusEnergy
            }
            ComponentType.EGG -> {
                if (energy < eggEnergy) return
                targetComponent.color = Color.BLACK
                targetComponent.die()
                ship.removeComponent(targetComponent.component)
                energy -= eggEnergy
            }
            else -> {}
        }
    }
}