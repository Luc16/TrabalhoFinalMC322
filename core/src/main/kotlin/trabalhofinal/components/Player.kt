package trabalhofinal.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import trabalhofinal.utils.IVector2
import java.util.LinkedList
import java.util.Queue
import kotlin.math.cos
import kotlin.math.sin

class Player(x: Float, y: Float, radius: Float,
             override val isWall: Boolean = false,
             override val texture: Texture? = null,
             override var color: Color = Color.RED,
             override val type: ComponentType = ComponentType.PLAYER
): Circle(x, y, radius), trabalhofinal.components.general.Component {

    //posicoes tile
    var pos = IVector2(0,0)
    val i: Int get() = pos.i
    val j: Int get() = pos.j

    var isMoving = false

    var destQueue: Queue<IVector2> = LinkedList()

    lateinit var targetComponent: TargetComponent

    var dir = Vector2(1f, 0f)
    var cameraPlane = Vector2(0f, 0.66f)
    fun rotate(angle: Float){
        // rotaciona o jogador utilizando multiplicando a direçao e plano da camera por uma matriz de rotação
        dir = Vector2(dir.x* cos(angle) - dir.y* sin(angle), dir.x* sin(angle) + dir.y* cos(angle))
        cameraPlane = Vector2(cameraPlane.x* cos(angle) - cameraPlane.y* sin(angle), cameraPlane.x* sin(angle) + cameraPlane.y* cos(angle))
    }

    fun move(i: Int, j: Int){ //TODO: receber um Ivector2
        pos.i = i
        pos.j = j
    }
}