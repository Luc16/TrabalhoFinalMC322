package trabalhofinal.utils.graphics

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram

// Vertex order: upper left, lower left, upper right, lower right
/** Creates a new Mesh with 8 vertices (quad) with the given attributes an texture.
 *
 * @param texture texture region for the mesh
 * @param vertices float array of mesh vertices in order: upper left, middle left, upper middle,
 * upper right, middle right, lower right, upper middle, lower left
 * */
class Textured2DMesh(
    var texture: Texture,
    private val vertices: FloatArray,
    var colorDiv: Float = 1f
) : Mesh(
    true, 100, 100, // TODO tirar o 100
    VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"), //x,y
    VertexAttribute(VertexAttributes.Usage.TextureCoordinates,2,"a_texCoord" + 0)
) {
    init {
        setVertices(vertices)
        setIndices(
            shortArrayOf(
                0, 1, 2,
                1, 2, 4,
                2, 3, 4,
                5, 6, 4,
                1, 6, 7
            )
        )
    }

    fun render(shader: ShaderProgram){
        render(shader, GL20.GL_TRIANGLE_STRIP)
    }


}