import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

class DeserializationException(e: Exception) : RuntimeException(e)

fun getNumbers(reader: BufferedReader): List<Int> {
    return reader.readLine()
        .split(":")
        .filter { it != "" }
        .map { it.toInt() }
        .toList()
}

fun getValues(reader: BufferedReader): List<String> {
    return reader.readLine()
        .split(":")
        .filter { it != "" }
}

fun deserializeTree(parents: List<NodeAST>, parentsNumbers: List<Int>, reader: BufferedReader) {
    if (parentsNumbers.all { it == 0 }) return
    val types = getValues(reader)
    val numbers = getNumbers(reader)
    val valuesNumbers = getNumbers(reader)
    val values = getValues(reader)

    val children = mutableListOf<NodeAST>()
    var idx = 0
    for (i in parents.indices) {
        for (j in 0 until parentsNumbers[i]) {
            val node = NodeAST(parents[i], types[idx + j])
            children.add(node)
            parents[i].addChild(node)
        }
        idx += parentsNumbers[i]
    }

    idx = 0
    for (i in children.indices) {
        for (j in 0 until valuesNumbers[i]) {
            children[i].addValue(values[idx + j])
        }
        idx += valuesNumbers[i]
    }

    deserializeTree(children, numbers, reader)
}

fun serializeTree(nodes: List<NodeAST>, writer: BufferedWriter) {
    if (nodes.isEmpty()) return
    val children = mutableListOf<NodeAST>()
    nodes.forEach {
        writer.write(it.type + ":")
        children.addAll(it.getChildren())
    }
    writer.write("\n")
    nodes.forEach {
        writer.write(it.getChildren().size.toString() + ":")
    }
    writer.write("\n")
    nodes.forEach {
        writer.write(it.getValues().size.toString() + ":")
    }
    writer.write("\n")
    nodes.forEach {
        it.getValues().forEach { value -> writer.write("$value:") }
    }
    writer.write("\n")
    serializeTree(children, writer)
}

fun serializeCompressionsResults(
    results: List<CompressionResult>,
    dir: File,
    fileName: String
) {
    val file = File(dir, fileName)
    file.bufferedWriter().use {
        for (result in results) {
            it.write(result.filePath + "\n")
            serializeTree(listOf(result.tree), it)
            serializeTree(listOf(result.compressedTree), it)
        }
    }
}

fun deserializeTree(reader: BufferedReader): NodeAST {
    val rootType = getValues(reader)[0]
    val numbers = getNumbers(reader)
    getNumbers(reader)
    val values = getValues(reader)
    val tree = NodeAST(null, rootType)
    tree.addValues(values)
    deserializeTree(listOf(tree), numbers, reader)
    return tree
}

fun deserializeCompressionsResults(file: File): List<CompressionResult> {
    val results = mutableListOf<CompressionResult>()
    file.bufferedReader().use { reader ->
        while (true) {
            try {
                val filePath = reader.readLine() ?: break
                val tree = deserializeTree(reader)
                val compressedTree = deserializeTree(reader)
                results.add(CompressionResult(filePath, tree, compressedTree))
            } catch (e: Exception) {
                throw DeserializationException(e)
            }
        }
    }

    return results
}