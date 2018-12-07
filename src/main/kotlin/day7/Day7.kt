package day7

import helper.readSampleInput
import java.io.File
import java.util.*

val pattern = "Step ([A-Z]) must be finished before step ([A-Z]) can begin\\.".toRegex()
const val WORK_MODIFIER = 0
const val WORKER_COUNT = 2

/*
* PFKQWJSVUXEMNIHGTYDOZACRLB
*
*/
fun main(args: Array<String>) {


    val instructions = readSampleInput(7, 1).parseInstructions()
    val graph = Graph(instructions)

    val instructionOrder = graph.getInstructionOrder().joinToString(separator = "")
    println("A: $instructionOrder")
    val executionTime = graph.getExecutionTime(WORKER_COUNT)
    println("B: $executionTime")
}

private fun File.parseInstructions(): List<Pair<Char, Char>> {
    return readLines()
            .map { pattern.matchEntire(it) }
            .map { it!!.destructured }
            .map { (first, second) -> Pair(first[0], second[0]) }
}

data class Node(val letter: Char) : Comparable<Node> {

    val next = sortedSetOf<Node>()
    val prerequisites = mutableSetOf<Node>()
    val completionTime = letter - 'A' + WORK_MODIFIER + 1

    var processed = false

    val ready: Boolean
        get() = prerequisites.all { it.processed }


    fun dfs(find: Node): List<Node> {
        if (next.contains(find)) {
            return Collections.singletonList(this)
        }

        val path = next.asSequence()
                .map { it.dfs(find) }
                .firstOrNull { it.isNotEmpty() }

        return path ?: emptyList()
    }

    fun addNext(node: Node) = next.add(node)

    fun resetState() {
        processed = false

        next.forEach { it.resetState() }
    }

    override fun compareTo(other: Node): Int {
        return letter.compareTo(other.letter)
    }
}

class Graph(instructions: Collection<Pair<Char, Char>>) {
    private val letterNodes = mutableMapOf<Char, Node>()
    private val rootNode = Node(' ')

    private fun addInstruction(instruction: Pair<Char, Char>) {
        val first = getNode(instruction.first)
        val second = getNode(instruction.second)
        second.prerequisites.add(first)

        val firstExits = nodeExists(instruction.first)
        if (!firstExits) {
            rootNode.addNext(first)
            first.addNext(second)
        } else {
            val firstToSecond = first.dfs(second)

            if (firstToSecond.isEmpty()) {
                first.addNext(second)
            }
        }
    }

    private fun nodeExists(letter: Char): Boolean = !rootNode.dfs(getNode(letter)).isEmpty()

    private fun getNode(letter: Char): Node {
        return letterNodes.getOrPut(letter) { Node(letter) }
    }

    fun getInstructionOrder(): List<Char> {
        val tmpNext = rootNode.next.toSortedSet()
        val order = mutableListOf<Char>()

        while (!getReadyNext(tmpNext).isEmpty()) {
            val node = startNode(tmpNext)
            order.add(node.letter)
            finishNode(node, tmpNext)
        }

        rootNode.resetState()
        return order
    }

    fun getExecutionTime(workerCount: Int): Int {
        val tmpNext = rootNode.next.toSortedSet()
        val workers = (1..workerCount).map { Worker(it) }

        var time = 0
        var busyCount = 0
        while (!getReadyNext(tmpNext).isEmpty() || busyCount > 0) {
            time++

            workers.filter { it.finished(time) }
                    .forEach { worker ->
                        if (worker.hasNode()) {
                            finishNode(worker.finish(), tmpNext)
                            busyCount--
                        }

                        if (worker.finished(time) && !getReadyNext(tmpNext).isEmpty()) {
                            worker.start(time, startNode(tmpNext))
                            busyCount++
                        }
                    }
        }

        rootNode.resetState()
        return time
    }

    private fun startNode(tmpNext: MutableSet<Node>): Node {
        val node = getReadyNext(tmpNext).first()
        tmpNext.remove(node)
        return node
    }

    fun getReadyNext(set: Set<Node>) = set.filter { it.ready }

    private fun finishNode(node: Node, tmpNext: MutableSet<Node>) {
        node.processed = true
        tmpNext.addAll(node.next)
    }

    init {
        instructions.forEach(this::addInstruction)
    }
}

data class Worker(val id: Int) {
    var currentNode: Node? = null
    var finishTime = 0

    fun finished(time: Int): Boolean {
        return time >= finishTime
    }

    fun hasNode(): Boolean {
        return currentNode != null
    }

    fun finish(): Node {
        val node = currentNode
        if (node == null) {
            throw IllegalStateException()
        } else {
            currentNode = null
            return node
        }
    }

    fun start(time: Int, node: Node) {
        currentNode = node
        finishTime = time + node.completionTime
    }

}