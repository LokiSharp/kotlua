package moe.slk.kotlua.number

fun floorDiv(a: Double, b: Double): Double = Math.floor(a / b)

fun floorMod(a: Double, b: Double) = when {
    (a > 0 && b == Double.POSITIVE_INFINITY
            || a < 0 && b == Double.NEGATIVE_INFINITY) -> a

    (a > 0 && b == Double.NEGATIVE_INFINITY
            || a < 0 && b == Double.POSITIVE_INFINITY) -> b

    else -> a - Math.floor(a / b) * b
}

fun shiftLeft(a: Long, n: Long): Long {
    val n1 = n.toInt()
    return if (n >= 0) a shl n1 else a.ushr(-n1)
}

fun shiftRight(a: Long, n: Long): Long {
    val n1 = n.toInt()
    return if (n >= 0) a.ushr(n1) else a shl -n1
}