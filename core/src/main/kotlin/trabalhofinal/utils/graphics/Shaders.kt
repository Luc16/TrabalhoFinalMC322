package trabalhofinal.utils.graphics

const val vertexShader = "attribute vec4 a_position;\n" +
        "attribute vec4 a_color;\n" +
        "attribute vec2 a_texCoord0;\n" +
        "uniform mat4 u_projTrans; \n" +
        "varying vec4 v_color;\n" +
        "varying vec2 v_texCoords;\n" +
        "void main() {\n" +
        " v_color = vec4(1, 1, 1, 1);\n" +
        " v_texCoords = a_texCoord0;\n" +
        " gl_Position =  u_projTrans * a_position;\n" +
        "}"
const val fragmentShader = "#ifdef GL_ES\n" +
        "precision mediump float; \n" +
        "#endif\n" +
        "varying vec4 v_color;\n" +
        "varying vec2 v_texCoords; \n" +
        "uniform sampler2D u_texture;\n" +
        "uniform float f_colorDiv;\n" +
        "void main() {\n" +
        " vec4 col = texture2D(u_texture, v_texCoords);" +
        " vec4 finalCol = vec4(col.r/f_colorDiv, col.g/f_colorDiv, col.b/f_colorDiv, 1);" +
        " gl_FragColor = v_color * finalCol + vec4(0, 0, 0, -0.50);\n" +
        "}"
