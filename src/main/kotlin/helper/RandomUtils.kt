package helper

fun <T> performTicksLooped(totalIterations: Long, initialState: T, tick: (T) -> T): T {
    val seenStates = linkedSetOf(initialState)

    val findMatching: (T) -> Pair<Int, T>? = {
        val index = seenStates.indexOf(it)
        if (seenStates.contains(it)) {
            Pair(index, it)
        } else {
            seenStates.add(it)
            null
        }
    }

    val finalIndex =
        findFinalIterationLoopIndex(initialState, totalIterations, tick, findMatching)

    return seenStates.elementAt(finalIndex)
}

private fun <T> findFinalIterationLoopIndex(
    initialState: T,
    totalIterations: Long,
    tick: (T) -> T,
    findMatching: (T) -> Pair<Int, T>?
): Int {
    var state = initialState

    var iteration = 0
    do {
        iteration++
        state = tick(state)
    } while (findMatching(state) != null && iteration < totalIterations)

    return if (iteration.toLong() == totalIterations) {
        iteration
    } else {
        val loopStart = findMatching(state)!!.first
        val loopSize = iteration - loopStart

        ((totalIterations - iteration) % loopSize + loopStart).toInt()
    }
}

