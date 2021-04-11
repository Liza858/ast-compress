import org.eclipse.jdt.core.dom.*

class NodeAST(var parent: NodeAST?, val type: String) {
    private val children = mutableListOf<NodeAST>()
    private val values = mutableListOf<String>()

    fun addAllChildren(children: List<NodeAST>) {
        this.children.addAll(children)
    }

    fun addChild(child: NodeAST) {
        children.add(child)
    }

    fun removeChild(child: NodeAST) {
        children.remove(child)
    }

    fun getChildren(): List<NodeAST> {
        return children
    }

    fun getValues(): List<String> {
        return values
    }

    fun addValue(value: String) {
        values.add(value)
    }

    fun addValues(values: List<String>) {
        this.values.addAll(values)
    }

    fun print() {
        print("")
    }

    private fun print(depth: String) {
        val space = "   "
        kotlin.io.print("$depth|——")
        kotlin.io.print(type)
        values.forEach {
            kotlin.io.print("\n$depth|$space|——$it")
        }
        println()
        for (child in children) {
            child.print("$depth|$space")
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other is NodeAST) && (this.type == other.type)
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }
}

fun buildAST(node: ASTNode): NodeAST {
    val root = NodeAST(null, "compilation unit")
    buildAST(root, node)
    return root
}

fun buildAST(parent: NodeAST, node: ASTNode) {
    for (property in node.structuralPropertiesForType()) {
        val descriptor = property as StructuralPropertyDescriptor
        val newNode = NodeAST(parent, property.id)
        val child = node.getStructuralProperty(descriptor)
        if (child == null) {
            parent.addChild(newNode)
            continue
        }
        when (descriptor) {
            is ChildPropertyDescriptor -> {
                buildAST(newNode, child as ASTNode)
            }
            is ChildListPropertyDescriptor -> {
                val nodeList = child as List<*>
                for (astNode in nodeList) {
                    if (astNode is ASTNode) buildAST(newNode, astNode)
                }
            }
            is SimplePropertyDescriptor -> {
                newNode.addValue(child.toString())
            }
        }
        parent.addChild(newNode)
    }
}