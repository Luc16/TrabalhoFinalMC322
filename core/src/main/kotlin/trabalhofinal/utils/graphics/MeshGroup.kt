package trabalhofinal.utils.graphics

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Disposable

class MeshGroup(private val shader: ShaderProgram, private val meshes: MutableList<Textured2DMesh>): Disposable {
    constructor(shader: ShaderProgram) : this(shader, mutableListOf())

    fun add(quad: Textured2DMesh) = meshes.add(quad)
    operator fun get(i: Int): Textured2DMesh = meshes[i]


    fun render(camera: Camera){
        shader.bind()
        shader.setUniformMatrix("u_projTrans", camera.combined)
        var texture: Texture? = null
        meshes.forEach { mesh ->
            if (texture == null || mesh.texture != texture) {
                texture = mesh.texture
                mesh.texture.bind()

            }
            shader.setUniformf("f_colorDiv", mesh.colorDiv)
            mesh.render(shader)
        }
    }

    override fun dispose(){
        meshes.forEach {
            it.dispose()
        }
    }


}