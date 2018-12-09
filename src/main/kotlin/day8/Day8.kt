import helper.readInput
import java.util.*

/*
 * A: 47, 4016
*  B: 46306
 */
fun main(args: Array<String>) {

    val scanner = Scanner(readInput(8))

    val tree = Node.buildTree(scanner)

    println("A: ${tree.metadataSum()}")
    println("B: ${tree.value()}")
}

data class Node(val children: List<Node>, val metadata: List<Int>) {

    fun metadataSum(): Int {
        return metadata.sum() + children.sumBy { it.metadataSum() }
    }

    fun value(): Int {
        if (children.isEmpty()) {
            return metadata.sum()
        }
        val childrenIndices = children.indices
        return metadata
            .map { it - 1 }
            .filter { childrenIndices.contains(it) }
            .sumBy{ children[it].value() }
    }

    companion object {
        fun buildTree(scanner: Scanner): Node {
            val childCount = scanner.nextInt()
            val metadataCount = scanner.nextInt()

            val children = mutableListOf<Node>()
            for (i in 0 until childCount) {
                children.add(buildTree(scanner))
            }

            val metadata = mutableListOf<Int>()
            for (i in 0 until metadataCount) {
                metadata.add(scanner.nextInt())
            }

            return Node(children, metadata)
        }
    }
}