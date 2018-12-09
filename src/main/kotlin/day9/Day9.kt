package day9

class MarbleGame(playerCount: Int, lastMarble: Int) {

    private val playerScores = mutableMapOf<Int, Long>()
    var currentMarble = MarbleNode(0)

    init {
        var currentPlayer = 0

        for (value in 1..lastMarble) {

            if (value % 23 == 0) {
                var score = playerScores.getOrDefault(currentPlayer, 0)
                score += value

                val removedMarble = currentMarble.counterClockwiseSteps(7)
                score += removedMarble.value

                currentMarble = removedMarble.next
                removedMarble.remove()

                playerScores[currentPlayer] = score
            } else {
                val node = MarbleNode(value)
                currentMarble.clockwiseSteps(1).insertAfter(node)
                currentMarble = node
            }

            currentPlayer = (currentPlayer + 1) % playerCount
        }
    }

    val highestScore: Long = playerScores.values.max()!!
}

data class MarbleNode(val value: Int) {
    var next: MarbleNode
    var previous: MarbleNode

    init {
        next = this
        previous = this
    }

    fun insertAfter(node: MarbleNode) {
        node.previous = this
        node.next = this.next

        this.next.previous = node
        this.next = node
    }

    fun insertBefore(node: MarbleNode) {
        node.next = this
        node.previous = this.previous

        this.previous.next = node
        this.previous = node
    }

    fun remove() {
        previous.next = next
        next.previous = previous

        next = this
        previous = this
    }

    fun clockwiseSteps(steps: Int): MarbleNode = if (steps == 0) {
        this
    } else {
        next.clockwiseSteps(steps - 1)
    }

    fun counterClockwiseSteps(steps: Int): MarbleNode = if (steps == 0) {
        this
    } else {
        previous.counterClockwiseSteps(steps - 1)
    }
}