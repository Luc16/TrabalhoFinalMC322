package trabalhofinal.exceptions

class InvalidTextureVertices(verticesSize: Int): Exception("Wrong number of vertices. Expected ${8*4} or ${4*4}, got $verticesSize")