package moe.slk.kotlua.number

/**
 * 整除
 * @param  a 被除数
 * @param  b 除数
 * @return 计算结果
 */
fun floorDiv(a: Double, b: Double): Double = Math.floor(a / b)

/**
 * 取模
 * @param  a 被除数
 * @param  b 除数
 * @return 计算结果
 */
fun floorMod(a: Double, b: Double) = when {
    (a > 0 && b == Double.POSITIVE_INFINITY
            || a < 0 && b == Double.NEGATIVE_INFINITY) -> a

    (a > 0 && b == Double.NEGATIVE_INFINITY
            || a < 0 && b == Double.POSITIVE_INFINITY) -> b

    else -> a - Math.floor(a / b) * b
}

/**
 * 左移运算
 * @param  a 被除数
 * @param  n 移动位数
 * @return 计算结果
 */
fun shiftLeft(a: Long, n: Long): Long {
    val n1 = n.toInt()
    return if (n >= 0) a shl n1 else a.ushr(-n1)
}

/**
 * 右移运算
 * @param  a 被除数
 * @param  n 移动位数
 * @return 计算结果
 */
fun shiftRight(a: Long, n: Long): Long {
    val n1 = n.toInt()
    return if (n >= 0) a.ushr(n1) else a shl -n1
}