package trabalhofinal.components

import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import kotlin.math.cos
import kotlin.math.sin

class Player(x: Float, y: Float, radius: Float): Circle(x, y, radius) {

    var dir = Vector2(1f, 0f)
    var cameraPlane = Vector2(0f, 0.66f)
    fun rotate(angle: Float){
        // rotaciona o jogador utilizando multiplicando a direçao e plano da camera por uma matriz de rotação
        dir = Vector2(dir.x* cos(angle) - dir.y* sin(angle), dir.x* sin(angle) + dir.y* cos(angle))
        cameraPlane = Vector2(cameraPlane.x* cos(angle) - cameraPlane.y* sin(angle), cameraPlane.x* sin(angle) + cameraPlane.y* cos(angle))
    }
}