package day4

import helper.readInput
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter


val generalPattern = """\[([\d-]+) ([\d:]+)] (.+?)""".toRegex()
val shiftStartPattern = """Guard #(\d+) begins shift""".toRegex()
val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")!!
val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")!!
val shiftEnd = LocalTime.MIDNIGHT.plusHours(1)!!

fun main(args: Array<String>) {

    // #1 @ 1,3: 4x4
    val actions = readInput(4)
        .readLines()
        .sorted()
        .map { splitLine(it) }
        .groupBy({ normalizeDate(it.first, it.second) }, { Pair(it.second, it.third) })
        .mapValues { (_, values) -> buildDay(values) }

    val overallSleepingMinutes = buildSleepingMap(actions)

    solve(overallSleepingMinutes, { (_, value) -> value.sum() }, "A")
    solve(overallSleepingMinutes, { (_, value) -> value.max()!! }, "B")
}

private fun solve(
    overallSleepingMinutes: Map<Int, Array<Int>>,
    findGuard: (Map.Entry<Int, Array<Int>>) -> Int,
    problemId: String
) {
    val mostSleepingGuard = overallSleepingMinutes.maxBy(findGuard)!!
    val guardId = mostSleepingGuard.key
    val popularMinute = findMostPopularMinute(mostSleepingGuard.value)
    println("$problemId: $guardId * $popularMinute = ${guardId * popularMinute}")
}

private fun buildSleepingMap(actions: Map<LocalDate, GuardDay>): Map<Int, Array<Int>> {
    val sleepingMinutes = mutableMapOf<Int, Array<Int>>()
    actions.forEach { (_, day) ->
        day.applyActions(sleepingMinutes)
    }
    return sleepingMinutes
}

fun normalizeDate(date: LocalDate, time: LocalTime): LocalDate {
    return if (time.hour == 0) date
    else date.plusDays(1)
}

private fun findMostPopularMinute(minutes: Array<Int>): Int = minutes.indexOf(minutes.max())

private fun parseTime(it: String) = LocalTime.parse(it, timeFormatter)!!
private fun parseDate(it: String) = LocalDate.parse(it, dateFormatter)!!

fun buildDay(lines: List<Pair<LocalTime, String>>): GuardDay {

    val actions = mutableListOf<GuardAction>()
    val guardId = shiftStartPattern.matchEntire(lines[0].second)!!.groupValues[1].toInt()

    for ((time, actionString) in lines.subList(1, lines.size)) {
        actions.add(buildGuardAction(time, actionString))
    }

    return GuardDay(guardId, actions)
}

private fun buildGuardAction(time: LocalTime, actionString: String): GuardAction {
    return when {
        actionString.startsWith("falls asleep") -> FallAsleep(time)
        actionString.startsWith("wakes up") -> WakeUp(time)
        else -> throw IllegalArgumentException("Unknown action")
    }
}

private fun splitLine(line: String): Triple<LocalDate, LocalTime, String> {
    val (date, time, actionString) = generalPattern.matchEntire(line)!!.destructured
    return Triple(parseDate(date), parseTime(time), actionString)
}

class GuardDay(private val id: Int, private val actions: List<GuardAction>) {

    fun applyActions(sleepingMinutes: MutableMap<Int, Array<Int>>) {
        val guardMinutes = sleepingMinutes.getOrPut(id) { Array(60) { 0 } }

        for (i in 0 until actions.lastIndex) {
            actions[i].applyActionUntil(guardMinutes, actions[i + 1].time)
        }

        if (!actions.isEmpty()) {
            actions.last().applyActionUntil(guardMinutes, shiftEnd)
        }
    }
}

abstract class GuardAction(val time: LocalTime) {
    abstract fun applyActionUntil(sleepingMinutes: Array<Int>, nextAction: LocalTime)
}

class FallAsleep(time: LocalTime) : GuardAction(time) {
    override fun applyActionUntil(sleepingMinutes: Array<Int>, nextAction: LocalTime) {
        for (i in time.minute..nextAction.minusMinutes(1).minute) {
            sleepingMinutes[i] += 1
        }
    }
}

class WakeUp(time: LocalTime) : GuardAction(time) {
    override fun applyActionUntil(sleepingMinutes: Array<Int>, nextAction: LocalTime) {

    }
}

