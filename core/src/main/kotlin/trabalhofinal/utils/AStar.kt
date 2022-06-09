package trabalhofinal.utils

class AStar {
    private val graph = Array(5) { i ->
        Array(5) { j ->
            Node(IVector2(j,i))
        }
    }

    init {
        graph[3][3].isWall = true
    }


    private fun IVector2.isOutOfRange(): Boolean = i < 0 || i >= graph.size || j < 0 || j >= graph[0].size
    private fun IVector2.isBlocked(): Boolean = graph[i][j].isWall
    private fun getNode(pos: IVector2): Node = graph[pos.i][pos.j]


    private fun MutableList<Node>.nextNode(): Node{
        var smaller = this[0]
        for (i in 1 until this.size) {
            if (this[i].f < smaller.f){
                smaller = this[i]
            }
        }
        return smaller
    }

    private fun Node.forEachNeighbor(func: (Node) -> Unit){
        listOf(-1, 1).forEach {idx ->
            if (idx + j in 0 until  graph[0].size)
                func(graph[i][idx + j])
            if (idx + i in graph.indices)
                func(graph[idx + i][j])
        }
    }

    private fun printPath(end: Node) {
        var node: Node = end.parent!!
        while (node.parent != null) {
            node.s = "."
            node = node.parent!!
        }
        printBoard()
    }

    fun findPath(source: IVector2, dest: IVector2): Boolean{
        // ver se a posicao destino eh valida
        if (dest.isOutOfRange() || source.isOutOfRange() || dest.isBlocked() || source.isBlocked()) return false
        val src = getNode(source)
        val end = getNode(dest)
        src.g = 0
        src.h = 0
        var foundDest = false
        val open = mutableListOf(src)

        while (open.isNotEmpty()){
            val node = open.nextNode()
            if (node == end) {
                printPath(end)
                return true
            }

            open.remove(node)
            node.wasVisited = true

            node.forEachNeighbor { neighbor ->
                // se eh parede ou neighbor possuir componente bloqueador, return
                if (neighbor.isWall || neighbor.wasVisited) return@forEachNeighbor

                if (neighbor.parent == null || neighbor.parent?.let {it.g > node.g} == true){
                    neighbor.parent = node
                    neighbor.g = node.g + 1
                    open.add(neighbor)
                }
                if (neighbor.h == Int.MAX_VALUE){
                    neighbor.h = abs(neighbor.i - end.i) + abs(neighbor.j - end.j)
                }


            }
        }
        return false
    }

    private fun printBoard(){
        graph.forEach {
            it.forEach {node ->
                print(node.s)
            }
            println()
        }
    }
}