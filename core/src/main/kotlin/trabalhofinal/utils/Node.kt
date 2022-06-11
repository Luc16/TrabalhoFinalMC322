package trabalhofinal.utils

open class Node(val pos: IVector2, val isWall: Boolean){

    constructor(i: Int, j: Int, isWall: Boolean): this(IVector2(j, i), isWall)

    var g = Int.MAX_VALUE
    var h = Int.MAX_VALUE
    val f: Int
        get() = g + h
    var parent: Node? = null
    var wasVisited = false
    val i: Int
        get() = pos.i
    val j: Int
        get() = pos.j

    fun reset(){
        g = Int.MAX_VALUE
        h = Int.MAX_VALUE
        parent = null
        wasVisited = false
    }

}