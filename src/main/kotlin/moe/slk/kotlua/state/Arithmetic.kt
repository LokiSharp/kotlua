package moe.slk.kotlua.state

import moe.slk.kotlua.api.ArithOp
import moe.slk.kotlua.number.floorDiv
import moe.slk.kotlua.number.floorMod
import moe.slk.kotlua.number.shiftLeft
import moe.slk.kotlua.number.shiftRight

private typealias IntegerOp = (Long, Long) -> Long
private typealias FloatOp = (Double, Double) -> Double

/**
 * 定义整形操作
 */
private val integerOps = listOf<IntegerOp?>(
    { a, b -> a + b },                  // LUA_OPADD
    { a, b -> a - b },                  // LUA_OPSUB
    { a, b -> a * b },                  // LUA_OPMUL
    { x, y -> Math.floorMod(x, y) },    // LUA_OPMOD
    null,                               // LUA_OPPOW
    null,                               // LUA_OPDIV
    { x, y -> Math.floorDiv(x, y) },    // LUA_OPIDIV
    { a, b -> a and b },                // LUA_OPBAND
    { a, b -> a or b },                 // LUA_OPBOR
    { a, b -> a xor b },                // LUA_OPBXOR
    ::shiftLeft,                        // LUA_OPSHL
    ::shiftRight,                       // LUA_OPSHR
    { a, _ -> -a },                     // LUA_OPUNM
    { a, _ -> a.inv() }                 // LUA_OPBNOT
)

/**
 * 定义浮点形操作
 */
private val floatOps = listOf<FloatOp?>(
    { a, b -> a + b },                  // LUA_OPADD
    { a, b -> a - b },                  // LUA_OPSUB
    { a, b -> a * b },                  // LUA_OPMUL
    ::floorMod,                         // LUA_OPMOD
    { a, b -> Math.pow(a, b) },         // LUA_OPPOW
    { a, b -> a / b },                  // LUA_OPDIV
    ::floorDiv,                         // LUA_OPIDIV
    null,                               // LUA_OPBAND
    null,                               // LUA_OPBOR
    null,                               // LUA_OPBXOR
    null,                               // LUA_OPSHL
    null,                               // LUA_OPSHR
    { a, _ -> -a },                     // LUA_OPUNM
    null                                // LUA_OPBNOT
)

/**
 * 调用操作
 * @param a 被操作数
 * @param b 操作数
 * @param op 操作符
 * @return 操作结果
 */
fun arith(a: Any?, b: Any?, op: ArithOp): Any? {
    val integerFunc = integerOps[op.ordinal]
    val floatFunc = floatOps[op.ordinal]

    if (floatFunc == null) { // bitwise
        val x = toInteger(a)
        if (x != null) {
            val y = toInteger(b)
            if (y != null) {
                return integerFunc!!(x, y)
            }
        }
    } else { // arith
        if (integerFunc != null
            && a is Long
            && b is Long
        ) { // add,sub,mul,mod,idiv,unm
            return integerFunc(a, b)
        }

        val x = toFloat(a)
        if (x != null) {
            val y = toFloat(b)
            if (y != null) {
                return floatFunc(x, y)
            }
        }
    }
    return null
}