class CompressionResult(
    val filePath: String,
    val tree: NodeAST,
    val compressedTree: NodeAST
) {
    fun print() {
        print("\nfile absolute path: $filePath\n")
        print("\nbefore compressions:\n\n")
        tree.print()
        print("\nafter compressions:\n\n")
        compressedTree.print()
    }

    private fun treesEquals(tree1: NodeAST, tree2: NodeAST): Boolean {
        if (tree1.type != tree2.type ||
            tree1.getValues() != tree2.getValues() ||
            tree1.getChildren().size != tree2.getChildren().size
        ) {
            return false
        }

        for (i in tree1.getChildren().indices) {
            if (!treesEquals(tree1.getChildren()[i], tree2.getChildren()[i])) {
                return false
            }
        }

        return true
    }

    override fun equals(other: Any?): Boolean {
        return (other is CompressionResult) &&
                (this.filePath == other.filePath) &&
                treesEquals(this.tree, other.tree) &&
                treesEquals(this.compressedTree, other.compressedTree)
    }
}
