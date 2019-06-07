package moe.slk.kotlua.state

/**
 * 实现堆栈
 * @property slots 用于存放值
 * @property top 用于记录栈顶索引
 */
internal class LuaStack {
    /* virtual stack */
    val slots = ArrayList<Any?>()
    /* call info */
    var closure: Closure? = null
    var varargs: List<Any>? = null
    var pc: Int = 0
    /* linked list */
    var prev: LuaStack? = null
    val top: Int
        inline get() = slots.size

    /**
     * 将值推入栈顶
     * @param value 待转换的对象
     */
    fun push(value: Any?) {
        if (slots.size > 10000) {
            throw StackOverflowError("")
        }
        slots.add(value)
    }

    /**
     * 从栈顶弹出一个值
     */
    fun pop() = slots.removeAt(slots.lastIndex)

    fun pushN(vals: List<Any>?, n: Int) {
        var n = n
        val nVals = vals?.size ?: 0
        if (n < 0) {
            n = nVals
        }
        for (i in 0 until n) {
            push(if (i < nVals) vals!![i] else null)
        }
    }

    fun popN(n: Int): List<Any> {
        val vals = java.util.ArrayList<Any>(n)
        for (i in 0 until n) {
            pop()?.let { vals.add(it) }
        }
        vals.reverse()
        return vals
    }

    /**
     * 将索引转换成绝对索引
     * @param idx 索引值
     */
    fun absIndex(idx: Int) = if (idx >= 0) idx else idx + top + 1

    /**
     * 判断索引是否有效
     * @param idx 索引值
     */
    fun isValid(idx: Int) = absIndex(idx) in 1..top

    /**
     * 从索引获取值
     * @param idx 索引值
     * @return 索引对应的值
     */
    fun get(idx: Int): Any? {
        val absIdx = absIndex(idx)

        return if (isValid(idx)) {
            slots[absIdx - 1]
        } else {
            null
        }
    }

    /**
     * 根据索引往栈里写入值
     * @param idx 索引值
     * @param value 索引对应的值
     */
    fun set(idx: Int, value: Any?) {
        val absIdx = absIndex(idx)

        if (isValid(idx)) {
            slots[absIdx - 1] = value
        } else {
            throw Exception("invalid index!")
        }
    }

    /**
     * 反转索引的值
     * @param from 原索引值
     * @param to 目标索引值
     */
    fun reverse(from: Int, to: Int) {
        slots.subList(from, to + 1).reverse()
    }
}

