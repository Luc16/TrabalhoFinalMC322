package trabalhofinal.utils.graphics

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram

// Vertex order: upper left, lower left, upper right, lower right
/** Creates a new Mesh with 4 vertices (quad) with the given attributes an texture.
 *
 * @param texture texture region for the mesh
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
        setVertices(vertices)
        setIndices(shortArrayOf(0, 1, 2, 1, 2, 3))
    }

    fun render(shader: ShaderProgram){
        render(shader, GL20.GL_TRIANGLE_STRIP)
    }


}