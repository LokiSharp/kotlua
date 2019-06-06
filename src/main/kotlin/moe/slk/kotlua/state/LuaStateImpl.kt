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
 * @param proto 函数原型
 * @property stack 用于存放堆栈
 * @property top 用于记录栈顶索引
 */
class LuaStateImpl(private val proto: Prototype) : LuaVM {
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

    /**
     * 执行算术和按位运算
     * @param op 运算符
     */
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

    /**
     * 对指定索引处的两个值进行比较
     * @param idx1 索引
     * @param idx2 索引
     * @param op 运算符
     * @return 布尔值
     */
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

    /**
     * 访问指定索引处的值，取其长度，然后推入栈顶
     * @param idx 索引
     */
    override fun len(idx: Int) {

        when (val value = stack.get(idx)) {
            is String -> pushInteger(value.length.toLong())
            is LuaTable -> pushInteger(value.getLength().toLong())
            else -> throw Exception("length error!")
        }
    }

    /**
     * 方法从栈顶弹出 n 个值，对这些值进行拼接，然后把结果推入栈顶
     * @param n 值的数量
     */
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

    /**
     * 获取程序计数器值
     * @return 程序计数器值
     */
    override fun getPC(): Int {
        return pc
    }

    /**
     * 增加程序计数器值
     * @param n 增加的值
     */
    override fun addPC(n: Int) {
        pc += n
    }

    /**
     * 根据 PC 索引从函数原型的指令表里取出当前指令，然后把 PC 加 1
     * @return 指令码
     */
    override fun fetch(): Int {
        return proto.code[pc++]
    }

    /**
     * 根据索引从函数原型的常量表里取出一个常量值，然后把它推入栈顶
     * @param idx 索引
     */
    override fun getConst(idx: Int) {
        stack.push(proto.constants[idx])
    }

    /**
     * 据情况调用 [getConst] 方法把某个常量推入栈顶，或者调用 [pushValue] 方法把某个索引处的栈值推入栈顶
     * @param rk 索引
     */
    override fun getRK(rk: Int) {
        if (rk > 0xFF) { // constant
            getConst(rk and 0xFF)
        } else { // register
            pushValue(rk + 1)
        }
    }

    /**
     * 创建一个尺寸为 0 的表，将其推入栈顶
     */
    override fun newTable() {
        createTable(0, 0)
    }

    /**
     * 创建一个空的表，将其推入栈顶
     * @param nArr 数组大小
     * @param nRec 哈希表大小
     */
    override fun createTable(nArr: Int, nRec: Int) {
        stack.push(LuaTable(nArr, nRec))
    }

    /**
     * 根据键获取表中对应值的类型
     * @param idx 索引
     * @return 对应值的类型
     */
    override fun getTable(idx: Int): LuaType {
        val t = stack.get(idx)
        val k = stack.pop()
        return getTable(t, k)
    }

    /**
     * 根据字符串获取相应索引的表中对应值的类型
     * @param idx 索引
     * @param k 字符串
     * @return 对应值的类型
     */
    override fun getField(idx: Int, k: String): LuaType {
        val t = stack.get(idx)
        return getTable(t, k)
    }

    /**
     * 根据数值获取相应索引的表中对应值的类型
     * @param idx 索引
     * @param  i 数值
     * @return 对应值的类型
     */
    override fun getI(idx: Int, i: Long): LuaType {
        val t = stack.get(idx)
        return getTable(t, i)
    }

    /**
     * 根据键从表里取值
     * @param t 表
     * @param k 键
     * @return 对应值的类型
     */
    private fun getTable(t: Any?, k: Any?): LuaType {
        if (t is LuaTable) {
            val v = t[k]
            stack.push(v)
            return typeOf(v)
        }
        throw Exception("not a table!") // todo
    }

    /* set functions (stack -> Lua) */

    /**
     * 把键值对写入表
     * @param idx 索引
     */
    override fun setTable(idx: Int) {
        val t = stack.get(idx)
        val v = stack.pop()
        val k = stack.pop()
        setTable(t, k, v)
    }

    /**
     * 把字符串写入表
     * @param idx 索引
     * @param k 字符串
     */
    override fun setField(idx: Int, k: String) {
        val t = stack.get(idx)
        val v = stack.pop()
        setTable(t, k, v)
    }

    /**
     * 把数值写入表
     * @param idx 索引
     * @param i 数值
     */
    override fun setI(idx: Int, i: Long) {
        val t = stack.get(idx)
        val v = stack.pop()
        setTable(t, i, v)
    }

    /**
     * 根据键从将值写入表中
     * @param t 表
     * @param k 键
     * @param v 值
     */
    private fun setTable(t: Any?, k: Any?, v: Any?) {
        if (t is LuaTable) {
            t.put(k, v)
            return
        }
        throw Exception("not a table!")
    }
}