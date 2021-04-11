fun compressTrees(trees: List<NodeAST>, numOfCompressions: Int): List<NodeAST> {
    val statistics = computeStatistics(trees)
    var compressedTrees = trees
    for (i in 0 until numOfCompressions) {
        val edgeToCompress = statistics.getTheMostCommonEdge() ?: continue
        compressedTrees = compressedTrees.map { compress(it, edgeToCompress, statistics) }
    }

    return compressedTrees
}

fun compress(parent: NodeAST, edgeToCompress: Edge, statistics: Statistics): NodeAST {
    val edgesToCompress = mutableListOf<Edge>()
    for (child in parent.getChildren()) {
        val edge = Edge(parent, child)
        if (edge == edgeToCompress) edgesToCompress.add(edge)
    }

    val newParent = compress(parent, edgeToCompress, edgesToCompress, statistics)
    val children = newParent.getChildren().toList()

    for (child in children) {
        compress(child, edgeToCompress, statistics)
    }

    return newParent
}

fun compress(
    parent: NodeAST,
    edgeToCompress: Edge,
    edgesToCompress: List<Edge>,
    statistics: Statistics
): NodeAST {
    if (edgesToCompress.isEmpty()) return parent

    val type = "(" + edgeToCompress.parent.type + " -> " + edgeToCompress.child.type + ")"
    val newNode = NodeAST(parent.parent, type)

    parent.getChildren().forEach { statistics.removeEdge(Edge(parent, it)) }

    newNode.addValues(parent.getValues())
    edgesToCompress.forEach { edge ->
        newNode.addAllChildren(edge.child.getChildren())
        newNode.addValues(edge.child.getValues())
        edge.child.getChildren().forEach {
            statistics.removeEdge(Edge(edge.child, it))
        }
        parent.removeChild(edge.child)
    }

    newNode.addAllChildren(parent.getChildren())
    newNode.getChildren().forEach {
        it.parent = newNode
        statistics.addEdge(Edge(newNode, it))
    }
    parent.parent?.let {
        it.removeChild(parent)
        it.addChild(newNode)
        statistics.removeEdge(Edge(it, parent))
        statistics.addEdge(Edge(it, newNode))
    }

    return newNode
}