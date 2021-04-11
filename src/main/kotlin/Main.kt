import org.eclipse.jdt.core.dom.AST
import org.eclipse.jdt.core.dom.ASTNode
import org.eclipse.jdt.core.dom.ASTParser
import java.io.File

fun parseProjectFiles(javaFiles: List<File>): List<ASTNode> {
    val parser = ASTParser.newParser(AST.JLS8)
    val trees = mutableListOf<ASTNode>()
    for (file in javaFiles) {
        parser.setSource(file.readText().toCharArray())
        parser.setKind(ASTParser.K_COMPILATION_UNIT)
        val ast = parser.createAST(null)
        trees.add(ast)
    }

    return trees
}

fun runCompress(
    pathToProject: File,
    numOfCompressions: Int,
    dir: File,
    fileName: String
): List<CompressionResult> {
    val javaFiles = pathToProject.walkTopDown().filter { it.extension == "java" }.toList()
    val trees = parseProjectFiles(javaFiles).map { buildAST(it) }.toList()
    val compressedTrees = compressTrees(trees, numOfCompressions)

    val results = mutableListOf<CompressionResult>()
    for (i in javaFiles.indices) {
        results.add(CompressionResult(javaFiles[i].absolutePath, trees[i], compressedTrees[i]))
    }

    results.forEach { it.print() }
    serializeCompressionsResults(results, dir, fileName)
    return results
}

fun runPrintDeserializedTrees(file: File) {
    try {
        deserializeCompressionsResults(file).forEach { it.print() }
    } catch (e: DeserializationException) {
        println("Error! File ${file.absolutePath} contains corrupted data or is not a file with trees.")
    }
}

fun main(args: Array<String>) {
    if (args.size != 1 && args.size != 4) {
        println("Error number of arguments")
        return
    }

    if (args.size == 1) {
        val file = File(args[0])
        if (!file.isFile) {
            println("This path ${args[0]} doesn't exist or is not a file")
            return
        }
        runPrintDeserializedTrees(file)
        return
    }

    val pathToProject = File(args[0])
    if (!pathToProject.isDirectory) {
        println("This path $pathToProject doesn't exist or is not a directory")
        return
    }

    val numOfCompressions = args[1].toIntOrNull()
    if (numOfCompressions == null || numOfCompressions < 0) {
        println("Wrong number of compressions: $numOfCompressions. It must be an integer number >= 0.")
        return
    }

    val dir = File(args[2])
    if (!dir.isDirectory) {
        println("This path $dir doesn't exist or is not a directory")
        return
    }

    runCompress(pathToProject, numOfCompressions, dir, args[3])
}