package day24

import kotlin.math.min

val pattern =
    """(\d+) units each with (\d+) hit points ((?:\([^)]+\) )?)?with an attack that does (\d+) ([a-z]+) damage at initiative (\d+)""".toRegex()

val selectionComparator: Comparator<UnitGroup> =
    compareBy<UnitGroup> { it.effectivePower() }.thenComparing { group: UnitGroup -> group.initiative }.reversed()

fun solveA(text: String): Int {
    val (immuneText, infectionText) = text.split("\n\n")
    val immune = unitGroups("Immune System", immuneText.trim())
    val infection = unitGroups("Infection", infectionText.trim())

    val (immuneCount, infectionCount) = solve(immune.toMutableList(), infection.toMutableList())

    return immuneCount + infectionCount
}

fun solveB(text: String): Int {
    val (immuneText, infectionText) = text.split("\n\n")
    val immune = unitGroups("Immune System", immuneText.trim())
    val infection = unitGroups("Infection", infectionText.trim())

    var boost = 1
    do {
        val boostedImmune = immune.map { it.boost(boost) }
        val copiedInfect = infection.map { it.copy() }

        val (immuneCount, infectionCount) = solve(boostedImmune, copiedInfect)
//        println("$immuneCount, $infectionCount")
        boost *= 2
    } while (immuneCount == 0 || (immuneCount > 0 && infectionCount > 0))

    boost /= 2
    var highestFailure = boost / 2
    var finalImmuneCount = 0

    while (boost - highestFailure > 1) {
        val mid = (boost + highestFailure) / 2

        val boostedImmune = immune.map { it.boost(mid) }
        val copiedInfect = infection.map { it.copy() }
        val (immuneCount, infectionCount) = solve(boostedImmune, copiedInfect)

        if (immuneCount > 0 && infectionCount == 0) {
            boost = mid
            finalImmuneCount = immuneCount
        } else {
            highestFailure = mid
        }
    }

    return finalImmuneCount
}

private fun solve(
    immuneIn: List<UnitGroup>,
    infectionIn: List<UnitGroup>
): Pair<Int, Int> {
    val immune = immuneIn.toMutableList()
    val infection = infectionIn.toMutableList()
    while (immune.any { it.alive() } && infection.any { it.alive() }) {
//        println("=============")
//        println("Selection Phase:")
//        println("Immune has ${immune.sumOf { it.count }} units")
//        println("Infection has ${infection.sumOf { it.count }} units")
        val attackPairs =
            selectAttackPairs(immune, infection).sortedByDescending { (attacker, _) -> attacker.initiative }
//        println("Attack Phase:")

        if (attackPairs.isEmpty()) {
            break;
        }

        var totalKilled = 0
        attackPairs.forEach { (attacker, defender) ->
            if (attacker.alive()) {
                val affectedUnits = defender.takeDamageFrom(attacker)
                totalKilled += affectedUnits

//                println("${attacker.name} attacks ${defender.name}, killing $affectedUnits units")
            }
        }

//        println("Total killed $totalKilled")
        immune.removeIf { !it.alive() }
        infection.removeIf { !it.alive() }
    }

    return immune.sumOf { it.count } to infection.sumOf { it.count }
}

private fun unitGroups(groupName: String, text: String): List<UnitGroup> {
    return text.lines().drop(1).mapIndexed { index, line -> parseUnits(line, "$groupName group ${index + 1}") }
}

private fun selectAttackPairs(
    immune: List<UnitGroup>,
    infection: List<UnitGroup>
): MutableList<Pair<UnitGroup, UnitGroup>> {
    val immuneSorted = immune.sortedWith(selectionComparator)
    val infectionSorted = infection.sortedWith(selectionComparator)

    val attackPairs = mutableListOf<Pair<UnitGroup, UnitGroup>>()
    attackPairs.addAll(selectTargets(infectionSorted, immuneSorted))
    attackPairs.addAll(selectTargets(immuneSorted, infectionSorted))
    return attackPairs
}

private fun selectTargets(
    attackers: List<UnitGroup>,
    defenders: List<UnitGroup>
): List<Pair<UnitGroup, UnitGroup>> {
    val attackPairs = mutableListOf<Pair<UnitGroup, UnitGroup>>()
    val chosen = mutableSetOf<UnitGroup>()
    attackers.forEach { attacker ->
        val filter = defenders.mapNotNull { defender ->
            if (defender in chosen) {
                null
            } else {
                val potentialDamage = defender.potentialDamageFrom(attacker)
//                println("${attacker.name} would deal ${defender.name} $potentialDamage damage [${defender.effectivePower()}, ${defender.initiative}] ")
                defender to potentialDamage
            }
        }
        val defenderPair = filter.maxByOrNull { (_, potentialDamage) -> potentialDamage }
        if (defenderPair != null && defenderPair.second > 0) {
//            println("${attacker.name} chooses ${defenderPair.first.name}")
            attackPairs.add(attacker to defenderPair.first)
            chosen.add(defenderPair.first)
        } else {
//            println("${attacker.name} has no target")
        }
    }
    return attackPairs
}

data class UnitGroup(
    val name: String, var count: Int, val hitPoints: Int,
    val immune: Set<String>, val weak: Set<String>,
    val attackDamage: Int, val damageType: String, val initiative: Int
) {

    fun alive(): Boolean = count > 0
    fun effectivePower() = count * attackDamage

    fun potentialDamageFrom(attacker: UnitGroup): Int {
        val effectivePower = attacker.effectivePower()
        return when {
            !alive() -> 0
            attacker.damageType in immune -> 0
            attacker.damageType in weak -> effectivePower * 2
            else -> effectivePower
        }
    }

    fun takeDamageFrom(attacker: UnitGroup): Int {
        return if (alive()) {
            val damage = potentialDamageFrom(attacker)
            val affectedUnits = min(damage / hitPoints, count)
            count -= affectedUnits

            affectedUnits
        } else {
            0
        }
    }

    fun boost(boost: Int): UnitGroup = copy(attackDamage = attackDamage + boost)

}

fun parseUnits(line: String, name: String): UnitGroup {
    val match = pattern.matchEntire(line)!!
    val count = match.groupValues[1].toInt()
    val hitPoints = match.groupValues[2].toInt()
    val weakImmune = match.groupValues[3].trim('(', ' ').trimEnd(')',' ').split("; ")
    val damageValue = match.groupValues[4].toInt()
    val damageType = match.groupValues[5]
    val initiative = match.groupValues[6].toInt()

    val weakSet =  extractSet(weakImmune, "weak to ")
    val immuneSet = extractSet(weakImmune, "immune to ")

    return UnitGroup(name, count, hitPoints, immuneSet, weakSet, damageValue, damageType, initiative)
}

fun extractSet(strings: List<String>, prefix: String): Set<String> = strings.firstOrNull { it.startsWith(prefix) }?.let { string ->
    string.substringAfter(prefix).split(", ").toSet()
} ?: emptySet()
