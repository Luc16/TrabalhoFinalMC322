package trabalhofinal.utils

class Node(private val pos: IVector2){
    var g = Int.MAX_VALUE
    var h = Int.MAX_VALUE
    val f: Int
        get() = g + h
    var parent: Node? = null
    var isWall = false
    var wasVisited = false
    val i: Int
        get() = pos.i
    val j: Int
        get() = pos.j
    var s = "0"
}