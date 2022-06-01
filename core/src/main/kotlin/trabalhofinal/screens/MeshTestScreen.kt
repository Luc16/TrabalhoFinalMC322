package trabalhofinal.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Circle
import com.badlogic.gdx.math.Vector2
import ktx.graphics.use
import trabalhofinal.HEIGHT
import trabalhofinal.MyGame
import trabalhofinal.WIDTH
import trabalhofinal.utils.graphics.QuadGroup
import trabalhofinal.utils.graphics.Textured2DQuad
import trabalhofinal.utils.graphics.fragmentShader
import trabalhofinal.utils.graphics.vertexShader
import kotlin.math.max
import kotlin.math.min

class MeshTestScreen(game: MyGame) : CustomScreen(game) {
    private val t = Texture(Gdx.files.internal("assets/wolftex/pics/mossy.png"))
    private val t2 = Texture(Gdx.files.internal("assets/wolftex/pics/colorstone.png"))
    private val quads = QuadGroup(
        ShaderProgram(vertexShader, fragmentShader),
        listOf(
            Textured2DQuad(t, floatArrayOf(
                200f, 660f,//upper left
                280f, 300f,//lower left
                600f, 660f, //upper right
                520f, 300f //lower right
            )),
            Textured2DQuad(t2, floatArrayOf(
                200f, 660f,//upper left
                280f, 300f,//lower left
                600f, 660f, //upper right
                520f, 300f //lower right
            ))
        )
    )
    private val camera: Camera = viewport.camera
    private val circles = listOf(
        Circle(200f, 660f, 10f),
        Circle(280f, 300f, 10f),
        Circle(600f, 660f, 10f),
        Circle(520f, 300f, 10f),
    )
    private var selectedIdx = -1

    override fun render(delta: Float) {
        quads[0].colorDiv = if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) 2f else 1f
        quads[0].setVertices(
            floatArrayOf(
                circles[0].x, circles[0].y, 0f, 0f, //upper left
                circles[1].x, circles[1].y, 0f, 1f, //lower left
                circles[2].x, circles[2].y, 1f, 0f, //upper right
                circles[3].x, circles[3].y, 1f, 1f, //lower right
            )
        )
        moveCircles()
        quads.render(camera)

        renderer.use(ShapeRenderer.ShapeType.Filled) {
            renderer.color = Color.WHITE
            circles.forEach {
                renderer.circle(it.x, it.y, it.radius)
            }
        }

    }

    override fun dispose() {
        quads.dispose()
        t.dispose()
        t2.dispose()
    }

    private fun moveCircles() {
        val mouse = Vector2(
            max(0f, min(Gdx.input.x.toFloat(), WIDTH)),
            max(0f, min(HEIGHT - Gdx.input.y.toFloat(), HEIGHT))
        )

        circles.forEachIndexed { i, it ->
            if (it.contains(mouse) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                selectedIdx = i
            }
        }
        if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) selectedIdx = -1
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && selectedIdx != -1) {
            circles[selectedIdx].x = mouse.x
            circles[selectedIdx].y = mouse.y
        }
    }

}