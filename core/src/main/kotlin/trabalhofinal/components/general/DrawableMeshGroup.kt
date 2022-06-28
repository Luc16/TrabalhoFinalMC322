package trabalhofinal.components.general

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable

interface DrawableMeshGroup: Disposable {
    fun render(camera: Camera, shader: ShaderProgram, initialX: Float = 0f, initialY: Float = 0f, ratio: Float = 1f)
}