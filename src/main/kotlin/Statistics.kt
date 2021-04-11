class Statistics {
    private val edgesNum = mutableMapOf<Edge, Int>()

    fun addEdge(edge: Edge) {
        edgesNum[edge] = edgesNum[edge]?.plus(1) ?: 1
    }

    fun removeEdge(edge: Edge) {
        edgesNum[edge] = edgesNum[edge]?.minus(1) ?: 0
        if (edgesNum[edge]?.compareTo(0) != 1) {
            edgesNum.remove(edge)
        }
    }

    fun getTheMostCommonEdge(): Edge? {
        val maxNum = edgesNum.maxByOrNull { it.value }?.value ?: return null
        return edgesNum.filterValues { it == maxNum }.keys.first()
    }
}

data class Edge(val parent: NodeAST, val child: NodeAST)

fun computeStatistics(parent: NodeAST, statistics: Statistics) {
    for (node in parent.getChildren()) {
        val edge = Edge(parent, node)
        statistics.addEdge(edge)
        computeStatistics(node, statistics)
    }
}

fun computeStatistics(roots: List<NodeAST>): Statistics {
    val statistics = Statistics()
    roots.forEach { root ->
        computeStatistics(root, statistics)
    }
    return statistics
}