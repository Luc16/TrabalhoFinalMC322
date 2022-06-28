package trabalhofinal.components.general

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import trabalhofinal.components.TargetComponent
import trabalhofinal.utils.AStar
import trabalhofinal.utils.IVector2
import java.util.LinkedList
import java.util.Queue
import kotlin.math.*

abstract class Player(tile: RayCastTile,
                      tileWidth: Float, tileHeight: Float,
                      texture: Texture, mapTexture: Texture,
):  RayCastComponent(tile, tileWidth, tileHeight, texture, mapTexture) {

    override val type = ComponentType.PLAYER
    abstract val name: String
    var live = true

    //posicoes tile
    var mapPos = IVector2(tile.i,tile.j)
    val i: Int get() = mapPos.i
    val j: Int get() = mapPos.j

    var energy = maxEnergy
    var isMoving = false
    var isSelected = false

    abstract val maxEnergy: Int
    abstract val webEnergy: Int
    abstract val fungusEnergy: Int
    abstract val eggEnergy: Int
    abstract val cameraPlaneSize: Float


    private var destQueue: Queue<IVector2> = LinkedList()
    private var pathLen = 0

    lateinit var targetComponent: TargetComponent

    var dir = Vector2(1f, 0f)
    var cameraPlane = Vector2(0f, cameraPlaneSize)
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
            this.dest = destQueue.first()
        }
    }

    override fun update(player: Player, zBuffer: List<Float>, tileWidth: Float, tileHeight: Float) {
        super.update(player, zBuffer, tileWidth, tileHeight)
        seen = true
    }

    fun updateSelected(tileWidth: Float, tileHeight: Float, mapRatio: Float, tile: RayCastTile) {
        if (!isMoving || destQueue.isEmpty()) return
        mapPos = IVector2(tile.i, tile.j)

        val speedY = mapRatio*tileWidth / 8
        val speedX = mapRatio*tileHeight / 8

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
                cameraPlane = Vector2(newDir.y, newDir.x).scl(if (abs(newDir.y) > 0f) -1*cameraPlaneSize else cameraPlaneSize)
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

    override fun draw(startX: Float, startY: Float, ratio: Float, batch: Batch) {
        if (!isSelected) batch.setColor(0.5f, 0.5f, 0.5f, 1f)
        super.draw(startX, startY, ratio, batch)
        batch.setColor(1f, 1f, 1f, 1f)
    }

    fun interact(ship: ComponentShip){
        // Se estiver muito longe não faz acao
        if (targetComponent.dist > 1.5f*sqrt(ship.tileHeight*ship.tileHeight + ship.tileWidth*ship.tileWidth)) return

        when(targetComponent.type){
            ComponentType.WEB -> {
                if (energy < webEnergy) return
                targetComponent.die()
                ship.removeComponent(targetComponent.component)
                energy -= webEnergy
            }
            ComponentType.FUNGUS -> {
                if (energy < fungusEnergy) return
                targetComponent.die()
                ship.removeComponent(targetComponent.component)
                ship.numFungi--
                energy -= fungusEnergy
            }
            ComponentType.EGG -> {
                if (energy < eggEnergy) return
                targetComponent.die()
                ship.removeComponent(targetComponent.component)
                energy -= eggEnergy
            }
            else -> {}
        }
    }

    override fun die() {
        live = false
        super.die()
    }
}