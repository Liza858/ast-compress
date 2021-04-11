import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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

fun getTypes(reader: BufferedReader): List<String> {
    return reader.readLine()
        .split(":")
        .filter { it != "" }
}

fun getValues(reader: BufferedReader): List<List<String>> {
    return Json.decodeFromString(reader.readLine())
}

fun deserializeTree(parents: List<NodeAST>, parentsNumbers: List<Int>, reader: BufferedReader) {
    if (parentsNumbers.all { it == 0 }) return
    val types = getTypes(reader)
    val numbers = getNumbers(reader)
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

    for (i in children.indices) {
        children[i].addValues(values[i])
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
    val nodesValues = mutableListOf<List<String>>()
    nodes.forEach {
        nodesValues.addAll(listOf(it.getValues()))
    }
    val string = Json.encodeToString(nodesValues)
    writer.write(string)
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
    val rootType = getTypes(reader)[0]
    val numbers = getNumbers(reader)
    val values = getValues(reader)
    val tree = NodeAST(null, rootType)
    tree.addValues(values[0])
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