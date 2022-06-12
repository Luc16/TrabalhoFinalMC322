package trabalhofinal.utils.graphics

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable

/** Group of meshes
 *
 * @param meshes a list of meshes, it is optional, if not provided the class will begin with an empty list
 * */
class MeshGroup(private val meshes: MutableList<Textured2DMesh>): Disposable {
    constructor() : this(mutableListOf())

    fun add(quad: Textured2DMesh) = meshes.add(quad)
    operator fun get(i: Int): Textured2DMesh = meshes[i]

    fun render(camera: Camera, shader: ShaderProgram, initialX: Float = 0f, initialY: Float = 0f, ratio: Float = 1f){
        shader.bind()
        // manda a matriz da camera para o shader
        shader.setUniformMatrix("u_projTrans", camera.combined)
        var texture: Texture? = null
        meshes.forEach { mesh ->
            // só troca a textura se a textura anterior for diferente a atual
            if (texture == null || mesh.texture != texture) {
                texture = mesh.texture
                mesh.texture.bind()
            }
            // ajusta as meshes
            mesh.moveAndScale(initialX, initialY, ratio)
            // divide a cor das meshes para ficar mais claro a imagem
            shader.setUniformf("f_colorDiv", mesh.colorDiv)
            mesh.render(shader)
        }
    }

    override fun dispose(){
        meshes.forEach {
            it.dispose()
        }
    }

    fun isEmpty(): Boolean = meshes.isEmpty()


}