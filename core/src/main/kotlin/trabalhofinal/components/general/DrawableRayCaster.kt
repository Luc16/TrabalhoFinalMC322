package trabalhofinal.components.general

import com.badlogic.gdx.math.Vector2

interface DrawableRayCaster {
    val collisionPoints: List<Vector2>
    val meshes: DrawableMeshGroup
    val floorLevel: Float
}