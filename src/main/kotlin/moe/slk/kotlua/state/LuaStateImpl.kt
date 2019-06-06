package moe.slk.kotlua.state

import moe.slk.kotlua.api.ArithOp
import moe.slk.kotlua.api.ArithOp.LUA_OPBNOT
import moe.slk.kotlua.api.ArithOp.LUA_OPUNM
import moe.slk.kotlua.api.CmpOp
import moe.slk.kotlua.api.CmpOp.*
import moe.slk.kotlua.api.LuaType
import moe.slk.kotlua.api.LuaType.*
import moe.slk.kotlua.api.LuaVM
import moe.slk.kotlua.binchunk.Prototype

/**
 * 实现堆栈操作
 * @property stack 用于存放堆栈
 * @property top 用于记录栈顶索引
 */
class LuaStateImpl(val proto: Prototype) : LuaVM {
    private val stack = LuaStack()
    private var pc = 0
    override var top: Int
        get() = stack.top
        set(idx) {
            val newTop = stack.absIndex(idx)
            if (newTop < 0) {
                throw RuntimeException("stack underflow!")
            }

            val n = stack.top - newTop
            if (n > 0) {
                for (i in 0 until n) {
                    stack.pop()
                }
            } else if (n < 0) {
                for (i in 0 downTo n + 1) {
                    stack.push(null)
                }
            }
        }

    /**
     * 将索引转换成绝对索引
     * @param idx 索引值
     */
    override fun absIndex(idx: Int): Int {
        return stack.absIndex(idx)
    }

    /**
     * 检查堆栈剩余空间是否能推入 n 个值
     * @param n 值的个数
     */
    override fun checkStack(n: Int): Boolean {
        return true // TODO
    }

    /**
     * 从栈顶弹出 n 个值
     * @param n 值的个数
     */
    override fun pop(n: Int) {
        for (i in 0 until n) {
            stack.pop()
        }
    }

    /**
     * 把值从一个索引复制到另一个索引
     * @param fromIdx 原索引
     * @param toIdx 目标索引
     */
    override fun copy(fromIdx: Int, toIdx: Int) {
        stack.set(toIdx, stack.get(fromIdx))
    }

    /**
     * 指定索引的值推入栈顶
     * @param idx 值的索引
     */
    override fun pushValue(idx: Int) {
        stack.push(stack.get(idx))
    }

    /**
     * 将栈顶值弹出，然后写入指定索引
     * @param idx 值的索引
     */
    override fun replace(idx: Int) {
        stack.set(idx, stack.pop())
    }

    /**
     * 将栈顶值弹出，然后插入指定位置
     * @param idx 值的索引
     */
    override fun insert(idx: Int) {
        rotate(idx, 1)
    }

    /**
     * 删除指定索引处的值，然后将该值上面的值全部下移一个位置
     * @param idx 值的索引
     */
    override fun remove(idx: Int) {
        rotate(idx, -1)
        pop(1)
    }

    /**
     * 将 [idx，top] 索引区间内的值朝栈顶方向旋转 n 个位置
     * @param idx 值的索引
     * @param n 位置数
     */
    override fun rotate(idx: Int, n: Int) {
        val t = stack.top - 1            /* end of stack segment being rotated */
        val p = stack.absIndex(idx) - 1    /* start of segment */
        val m = if (n >= 0) t - n else p - n - 1 /* end of prefix */

        stack.reverse(p, m)     /* reverse the prefix with length 'n' */
        stack.reverse(m + 1, t) /* reverse the suffix */
        stack.reverse(p, t)     /* reverse the entire segment */
    }

    /* access functions (stack -> Kotlin); */

    /**
     * 将类型转换为相应字符串
     * @param tp 类型
     * @return 字符串
     */
    override fun typeName(tp: LuaType) = when (tp) {
        LUA_TNONE -> "no value"
        LUA_TNIL -> "nil"
        LUA_TBOOLEAN -> "boolean"
        LUA_TNUMBER -> "number"
        LUA_TSTRING -> "string"
        LUA_TTABLE -> "table"
        LUA_TFUNCTION -> "function"
        LUA_TTHREAD -> "thread"
        else -> "userdata"
    }

    /**
     * 根据索引返回值的类型
     * @param idx 索引
     * @return 值类型
     */
    override fun type(idx: Int) = if (stack.isValid(idx)) typeOf(stack.get(idx)) else LUA_TNONE

    /**
     * 判断索引处是否为 None
     * @param idx 索引
     * @return 布尔值
     */
    override fun isNone(idx: Int) = type(idx) === LUA_TNONE

    /**
     * 判断索引处是否为 Nil
     * @param idx 索引
     * @return 布尔值
     */
    override fun isNil(idx: Int) = type(idx) === LUA_TNIL

    /**
     * 判断索引处是否为 None 或 Nil
     * @param idx 索引
     * @return 布尔值
     */
    override fun isNoneOrNil(idx: Int): Boolean {
        val t = type(idx)
        return t === LUA_TNONE || t === LUA_TNIL
    }

    /**
     * 判断索引处是否为布尔值
     * @param idx 索引
     * @return 布尔值
     */
    override fun isBoolean(idx: Int) = type(idx) === LUA_TBOOLEAN

    /**
     * 判断索引处是否为整型
     * @param idx 索引
     * @return 布尔值
     */
    override fun isInteger(idx: Int) = stack.get(idx) is Long

    /**
     * 判断索引处是否为浮点型
     * @param idx 索引
     * @return 布尔值
     */
    override fun isNumber(idx: Int) = toNumberX(idx) != null

    /**
     * 判断索引处是否为字符串
     * @param idx 索引
     * @return 布尔值
     */
    override fun isString(idx: Int): Boolean {
        val t = type(idx)
        return t === LUA_TSTRING || t === LUA_TNUMBER
    }

    /**
     * 判断索引处是否为表
     * @param idx 索引
     * @return 布尔值
     */
    override fun isTable(idx: Int) = type(idx) === LUA_TTABLE

    /**
     * 判断索引处是否为线程
     * @param idx 索引
     * @return 布尔值
     */
    override fun isThread(idx: Int) = type(idx) === LUA_TTHREAD

    /**
     * 判断索引处是否为函数
     * @param idx 索引
     * @return 布尔值
     */
    override fun isFunction(idx: Int) = type(idx) === LUA_TFUNCTION

    /**
     * 从索引取出值并转换为布尔值
     * @param idx 索引
     * @return 布尔值
     */
    override fun toBoolean(idx: Int) = toBoolean(stack.get(idx))

    /**
     * 从索引取出值并转换为整型，如果失败返回 0
     * @param idx 索引
     * @return 整值值
     */
    override fun toInteger(idx: Int) = toIntegerX(idx) ?: 0

    /**
     * 从索引取出值并转换为整型，如果失败返回 null
     * @param idx 索引
     * @return 整型值或 null
     */
    override fun toIntegerX(idx: Int): Long? {
        val value = stack.get(idx)
        return if (value is Long) value else null
    }

    /**
     * 从索引取出值并转换为浮点型，如果失败返回 0
     * @param idx 索引
     * @return 浮点型值
     */
    override fun toNumber(idx: Int) = toNumberX(idx) ?: 0.toDouble()

    /**
     * 从索引取出值并转换为浮点型，如果失败返回 null
     * @param idx 索引
     * @return 浮点型值或 null
     */
    override fun toNumberX(idx: Int): Double? = when (val value = stack.get(idx)) {
        is Double -> value
        is Long -> value.toDouble()
        else -> null
    }

    /**
     * 从索引取出值并转换为字符串，如果失败返回 null
     * @param idx 索引
     * @return 字符串或 null
     */
    override fun toString(idx: Int): String? = when (val value = stack.get(idx)) {
        is String -> value
        is Long, is Double -> value.toString()
        else -> null
    }

    /* push functions (Kotlin -> stack); */

    /**
     * 将空值推入栈顶
     */
    override fun pushNil() {
        stack.push(null)
    }

    /**
     * 将布尔值推入栈顶
     * @param b 布尔值
     */
    override fun pushBoolean(b: Boolean) {
        stack.push(b)
    }

    /**
     * 将整型值推入栈顶
     * @param n 整型值
     */
    override fun pushInteger(n: Long) {
        stack.push(n)
    }

    /**
     * 将浮点型值推入栈顶
     * @param n 浮点型值
     */
    override fun pushNumber(n: Double) {
        stack.push(n)
    }

    /**
     * 将字符串推入栈顶
     * @param s 字符串
     */
    override fun pushString(s: String) {
        stack.push(s)
    }

    override fun arith(op: ArithOp) {
        val b = stack.pop()
        val a = if (op != LUA_OPUNM && op != LUA_OPBNOT) {
            stack.pop()
        } else {
            b
        }

        val result = arith(a, b, op)
        if (result != null) {
            stack.push(result)
        } else {
            throw Exception("arithmetic error!")
        }
    }

    override fun compare(idx1: Int, idx2: Int, op: CmpOp): Boolean {
        if (!stack.isValid(idx1) || !stack.isValid(idx2)) {
            return false
        }

        val a = stack.get(idx1)
        val b = stack.get(idx2)

        return when (op) {
            LUA_OPEQ -> eq(a, b)
            LUA_OPLT -> lt(a!!, b!!)
            LUA_OPLE -> le(a!!, b!!)
        }
    }

    override fun len(idx: Int) {
        val value = stack.get(idx)

        if (value is String) {
            pushInteger(value.length.toLong())
        } else {
            throw Exception("length error!")
        }
    }

    override fun concat(n: Int) {
        if (n == 0) {
            stack.push("")
        } else if (n >= 2) {

            repeat(n - 1) {
                if (!isString(-1) || !isString(-2)) {
                    throw Exception("concatenation error!")
                }

                val s2 = toString(-1)
                val s1 = toString(-2)
                pop(2)
                pushString(s1 + s2)
            }
        }

        // n == 1, do nothing
    }

    /* LuaVM */
    override fun getPC(): Int {
        return pc
    }

    override fun addPC(n: Int) {
        pc += n
    }

    override fun fetch(): Int {
        return proto.code[pc++]
    }

    override fun getConst(idx: Int) {
        stack.push(proto.constants[idx])
    }

    override fun getRK(rk: Int) {
        if (rk > 0xFF) { // constant
            getConst(rk and 0xFF)
        } else { // register
            pushValue(rk + 1)
        }
    }
}