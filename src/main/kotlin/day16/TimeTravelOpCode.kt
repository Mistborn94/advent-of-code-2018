package day16

enum class TimeTravelOpCode(val description: String) {
    ADDR("Add Register") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = registers[a] + registers[b]
    },
    ADDI("Add Immediate") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = registers[a] + b
    },
    MULR("Multiply Register") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = registers[a] * registers[b]
    },
    MULI("Multiply Immediate") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = registers[a] * b
    },
    BANR("bitwise AND register") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = registers[a] and registers[b]
    },
    BANI("bitwise AND immediate") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = registers[a] and b
    },
    BORR("bitwise AND register") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = registers[a] or registers[b]
    },
    BORI("bitwise AND immediate") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = registers[a] or b
    },
    SETR("set register") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = registers[a]
    },
    SETI("set immediate") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = a
    },
    GTIR("greater-than immediate/register") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = if (a > registers[b]) 1 else 0
    },
    GTRI("greater-than register/immediate") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = if (registers[a] > b) 1 else 0
    },
    GTRR("greater-than register/register") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = if (registers[a] > registers[b]) 1 else 0
    },
    ETIR("equal immediate/register") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = if (a == registers[b]) 1 else 0
    },
    ETRI("equal register/immediate") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = if (registers[a] == b) 1 else 0
    },
    ETRR("equal register/register") {
        override fun getResult(registers: List<Int>, a: Int, b: Int): Int = if (registers[a] == registers[b]) 1 else 0
    };

    protected abstract fun getResult(registers: List<Int>, a: Int, b: Int): Int

    fun apply(registers: List<Int>, a: Int, b: Int, c: Int): List<Int> {
        val newRegisters = registers.toMutableList()
        newRegisters[c] = getResult(registers, a, b)
        return newRegisters
    }

    companion object {
        fun fromOperatinString(operation: String): TimeTravelOpCode {
            return TimeTravelOpCode.valueOf(operation.toUpperCase())
        }
    }
}