package moe.slk.kotlua.state

fun eq(a: Any?, b: Any?) = when (a) {
    null -> b == null
    is Boolean, is String -> a == b
    is Long -> a == b || (b is Double && b == a.toDouble())
    is Double -> a == b || (b is Long && a == b.toDouble())
    else -> a === b
}

fun lt(a: Any, b: Any) = when {
    a is String && b is String -> a < b
    a is Long && b is Long -> a < b
    a is Long && b is Double -> a.toDouble() < b
    a is Double && b is Double -> a < b
    a is Double && b is Long -> a < b.toDouble()
    else -> throw Exception("comparison error!")
}


fun le(a: Any, b: Any) = when {
    a is String && b is String -> a <= b
    a is Long && b is Long -> a <= b
    a is Long && b is Double -> a.toDouble() <= b
    a is Double && b is Double -> a <= b
    a is Double && b is Long -> a <= b.toDouble()
    else -> throw Exception("comparison error!")
}