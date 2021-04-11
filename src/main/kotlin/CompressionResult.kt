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
}
