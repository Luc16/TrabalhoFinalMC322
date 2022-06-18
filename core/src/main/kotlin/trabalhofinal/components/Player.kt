package trabalhofinal.components

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import trabalhofinal.utils.IVector2
import java.util.LinkedList
import java.util.Queue
import kotlin.math.cos
import kotlin.math.sin

class Player(x: Float, y: Float, radius: Float): Circle(x, y, radius) {

    //posicoes tile
    var pos = IVector2(0,0)
    var isMoving = false

    var destQueue: Queue<IVector2> = LinkedList()

    lateinit var aimingComponent: Pair<Component, Float>

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