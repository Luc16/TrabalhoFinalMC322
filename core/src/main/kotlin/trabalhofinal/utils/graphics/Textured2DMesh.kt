package trabalhofinal.utils.graphics

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram

// Vertex order: upper left, lower left, upper right, lower right
/** Creates a new Mesh with 8 vertices (quad) with the given attributes an texture.
 *
 * @param texture texture region for the mesh
 * @param vertices float array of mesh vertices in order: upper left, middle left, upper middle,
 * upper right, middle right, lower right, upper middle, lower left
 * or upper left, upper right, lower right, lower left
 * */
class Textured2DMesh(
    var texture: Texture,
    vertices: FloatArray,
    var colorDiv: Float = 1f
) : Mesh(
    true, 8, 15,
    VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"), //x,y
    VertexAttribute(VertexAttributes.Usage.TextureCoordinates,2,"a_texCoord" + 0)
) {
    init {
        when (vertices.size){
            8 * 4 -> setVertices(vertices)
            4 * 4 -> {
                fun mean(f1: Float, f2: Float) = (f1 + f2) / 2
                setVertices(
                    floatArrayOf(
                        vertices[0], vertices[1], vertices[2], vertices[3], // upper left
                        vertices[0], mean(vertices[1], vertices[13]), vertices[2], mean(vertices[3], vertices[15]), // middle left
                        mean(vertices[0], vertices[4]), mean(vertices[1], vertices[5]), mean(vertices[2], vertices[6]), vertices[3], // upper middle
                        vertices[4], vertices[5], vertices[6], vertices[7], // upper right
                        vertices[4], mean(vertices[5], vertices[9]), vertices[6], mean(vertices[7], vertices[11]), // middle right
                        vertices[8], vertices[9], vertices[10], vertices[11], // lower right
                        mean(vertices[8], vertices[12]), mean(vertices[9], vertices[13]), mean(vertices[10], vertices[14]), vertices[11], // lower middle
                        vertices[12], vertices[13], vertices[14], vertices[15], // lower left
                    )
                )
            }
            else -> throw Exception("Wrong number of vertices. Expected ${8*4} or ${4*4}, got ${vertices.size}")
        }

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


    fun moveAndScale(initialX: Float = 0f, initialY: Float = 0f, ratio: Float = 1f){
        val vertices = FloatArray(8*4)
        getVertices(vertices)
        setVertices(floatArrayOf(
            initialX + vertices[0]*ratio, initialY + vertices[1]*ratio, vertices[2], vertices[3],
            initialX + vertices[4]*ratio, initialY + vertices[5]*ratio, vertices[6], vertices[7],
            initialX + vertices[8]*ratio, initialY + vertices[9]*ratio, vertices[10], vertices[11],
            initialX + vertices[12]*ratio, initialY + vertices[13]*ratio, vertices[14], vertices[15],
            initialX + vertices[16]*ratio, initialY + vertices[17]*ratio, vertices[18], vertices[19],
            initialX + vertices[20]*ratio, initialY + vertices[21]*ratio, vertices[22], vertices[23],
            initialX + vertices[24]*ratio, initialY + vertices[25]*ratio, vertices[26], vertices[27],
            initialX + vertices[28]*ratio, initialY + vertices[29]*ratio, vertices[30], vertices[31],
        ))
    }
    fun render(shader: ShaderProgram){
        render(shader, GL20.GL_TRIANGLE_STRIP)
    }

    fun standAloneRender(shader: ShaderProgram){
        texture.bind()
        shader.setUniformf("f_colorDiv", colorDiv)
        render(shader)
    }


}