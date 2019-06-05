package moe.slk.kotlua.number

fun isInteger(f: Double): Boolean {
    return f == f.toLong().toDouble()
}

fun parseInteger(str: String): Long? {
    return try {
        str.toLong()
    } catch (e: NumberFormatException) {
        null
    }

}

fun parseFloat(str: String): Double? {
    return try {
        str.toDouble()
    } catch (e: NumberFormatException) {
        null
    }
}