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

/*
A: 733 * 48 = 35184
B: 997 * 38 = 37886
 */
fun main(args: Array<String>) {

    val days = readInput(4)
        .readLines()
        .sorted()
        .map { splitLine(it) }
        .groupBy({ normalizeDate(it.first, it.second) }, { Pair(it.second, it.third) })
        .values
        .map { values -> buildDay(values) }

    val overallSleepingMinutes = buildSleepingMap(days)

    solve("A", overallSleepingMinutes) { (_, value) -> value.sum() }
    solve("B", overallSleepingMinutes) { (_, value) -> value.max()!! }
}

private fun solve(
    problemId: String,
    overallSleepingMinutes: Map<Int, Array<Int>>,
    findGuard: (Map.Entry<Int, Array<Int>>) -> Int
) {
    val mostSleepingGuard = overallSleepingMinutes.maxBy(findGuard)!!
    val guardId = mostSleepingGuard.key
    val popularMinute = mostSleepingGuard.value.indexOfMax()
    println("$problemId: $guardId * $popularMinute = ${guardId * popularMinute}")
}

private fun buildSleepingMap(days: List<GuardDay>): Map<Int, Array<Int>> {
    val sleepingMinutes = mutableMapOf<Int, Array<Int>>()
    days.forEach { day ->
        day.applyActions(sleepingMinutes)
    }
    return sleepingMinutes
}

fun normalizeDate(date: LocalDate, time: LocalTime): LocalDate {
    return if (time.hour == 0) date
    else date.plusDays(1)
}

private fun Array<Int>.indexOfMax(): Int = indexOf(max())

private fun parseTime(it: String) = LocalTime.parse(it, timeFormatter)!!
private fun parseDate(it: String) = LocalDate.parse(it, dateFormatter)!!

fun buildDay(lines: List<Pair<LocalTime, String>>): GuardDay {

    val guardId = shiftStartPattern.matchEntire(lines[0].second)!!.groupValues[1].toInt()

    val actions= lines.subList(1, lines.size)
        .map { (time, action) -> buildGuardAction(time, action) }

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
        val endTime = nextAction.minusMinutes(1)
        for (i in time.minute..endTime.minute) {
            sleepingMinutes[i] += 1
        }
    }
}

class WakeUp(time: LocalTime) : GuardAction(time) {
    override fun applyActionUntil(sleepingMinutes: Array<Int>, nextAction: LocalTime) {

    }
}

