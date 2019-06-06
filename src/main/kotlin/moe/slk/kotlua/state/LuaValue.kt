package moe.slk.kotlua.state

import moe.slk.kotlua.api.LuaType.*
import moe.slk.kotlua.number.isInteger
import moe.slk.kotlua.number.parseFloat
import moe.slk.kotlua.number.parseInteger

/**
 * 检查对象类型
 * @param value 待检查的对象
 * @return 对象类型
 */
fun typeOf(value: Any?) = when (value) {
    null -> LUA_TNIL
    is Boolean -> LUA_TBOOLEAN
    is Long, is Double -> LUA_TNUMBER
    is String -> LUA_TSTRING
    is LuaTable -> LUA_TTABLE
    else -> throw Exception("Don't support ${value::class.simpleName}")
}

/**
 * 将对象转换为布尔型
 * @param value 待转换的对象
 * @return 布尔型
 */
fun toBoolean(value: Any?) = when (value) {
    null -> false
    is Boolean -> value
    else -> true
}

// http://www.lua.org/manual/5.3/manual.html#3.4.3

/**
 * 将对象转换为浮点型
 * @param value 待转换的对象
 * @return 浮点型
 */
fun toFloat(value: Any?) = when (value) {
    is Double -> value
    is Long -> value.toDouble()
    is String -> parseFloat(value)
    else -> null
}

// http://www.lua.org/manual/5.3/manual.html#3.4.3

/**
 * 将对象转换为整型
 * @param value 待转换的对象
 * @return 整型
 */
fun toInteger(value: Any?) = when (value) {
    is Long -> value
    is Double -> if (isInteger(value)) value.toLong() else null
    is String -> toInteger(value)
    else -> null
}

/**
 * 将字符串对象转换为整型
 * @param  s 待转换的字符串对象
 * @return 整型
 */
private fun toInteger(s: String): Long? {
    val i = parseInteger(s)
    if (i != null) {
        return i
    }
    val f = parseFloat(s)
    return if (f != null && isInteger(f)) f.toLong() else null
}