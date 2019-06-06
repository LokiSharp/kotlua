package moe.slk.kotlua.state

/**
 * 比较 a 是否等于 b
 * @param  a 被比较数
 * @param  b 比较数
 * @return 布尔值
 */
fun eq(a: Any?, b: Any?) = when (a) {
    null -> b == null
    is Boolean, is String -> a == b
    is Long -> a == b || (b is Double && b == a.toDouble())
    is Double -> a == b || (b is Long && a == b.toDouble())
    else -> a === b
}

/**
 * 比较 a 是否小于 b
 * @param  a 被比较数
 * @param  b 比较数
 * @return 布尔值
 */
fun lt(a: Any, b: Any) = when {
    a is String && b is String -> a < b
    a is Long && b is Long -> a < b
    a is Long && b is Double -> a.toDouble() < b
    a is Double && b is Double -> a < b
    a is Double && b is Long -> a < b.toDouble()
    else -> throw Exception("comparison error!")
}

/**
 * 比较 a 是否大于 b
 * @param  a 被比较数
 * @param  b 比较数
 * @return 布尔值
 */
fun le(a: Any, b: Any) = when {
    a is String && b is String -> a <= b
    a is Long && b is Long -> a <= b
    a is Long && b is Double -> a.toDouble() <= b
    a is Double && b is Double -> a <= b
    a is Double && b is Long -> a <= b.toDouble()
    else -> throw Exception("comparison error!")
}