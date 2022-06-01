package trabalhofinal.utils

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram

class QuadGroup(private val shader: ShaderProgram, quads: List<Textured2DQuad>) {
    private val quads = quads.toMutableList()

    constructor(shader: ShaderProgram) : this(shader, listOf())

    fun add(quad: Textured2DQuad) = quads.add(quad)
    operator fun get(i: Int): Textured2DQuad = quads[i]


    fun render(camera: Camera){
        shader.bind()
        shader.setUniformMatrix("u_projTrans", camera.combined)
        var texture: Texture? = null
        quads.forEach { quad ->
            if (texture == null || quad.texture != texture) {
                texture = quad.texture
                quad.texture.bind()

            }
            shader.setUniformf("f_colorDiv", quad.colorDiv)
            quad.render(shader)
        }
    }

    fun dispose(){
        quads.forEach {
            it.dispose()
        }
    }

}