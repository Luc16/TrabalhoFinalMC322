package trabalhofinal.utils

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram

// Vertex order: upper left, lower left, upper right, lower right
/** Creates a new Mesh with 4 vertices (quad) with the given attributes an texture.
 *
 * @param texture whether this mesh is static or not. Allows for internal optimizations.
 * @param vertices quad vertices in order: upper left, lower left, upper right, lower right
 * */
class Textured2DQuad(
    val texture: Texture,
    private val vertices: FloatArray,
    var colorDiv: Float = 1f
) : Mesh(
    true, 4, 6,
    VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"), //x,y
    VertexAttribute(VertexAttributes.Usage.TextureCoordinates,2,"a_texCoord" + 0)
) {
    init {
        setVertices(floatArrayOf(
            vertices[0], vertices[1], 0f,0f, //upper left
            vertices[2], vertices[3], 0f, 1f, //lower left
            vertices[4], vertices[5], 1f, 0f, //upper right
            vertices[6], vertices[7], 1f, 1f, //lower right
        ))
        setIndices(shortArrayOf(0, 1, 2, 1, 2, 3))
    }

    fun render(shader: ShaderProgram){
        render(shader, GL20.GL_TRIANGLE_STRIP)
    }


}