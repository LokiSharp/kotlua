package moe.slk.kotlua.state

import moe.slk.kotlua.number.isInteger
import java.util.*


/**
 * 定义表
 * @property arr 数组
 * @property map 哈希表
 */
internal class LuaTable(nArr: Int, nRec: Int) {

    private var arr: MutableList<Any?>? = null
    private var map: MutableMap<Any?, Any>? = null

    init {
        if (nArr > 0) {
            arr = ArrayList(nArr)
        }
        if (nRec > 0) {
            map = HashMap(nRec)
        }
    }

    fun length() = if (arr == null) 0 else arr!!.size

    /**
     * 根据键从表里取出值
     * @param key 键
     * @return 键对应值
     */
    operator fun get(key: Any?): Any? {
        val i = floatToInt(key)

        if (arr != null && i is Long) {
            val idx = i.toInt()
            if (idx >= 1 && idx <= arr!!.size) {
                return arr!![idx - 1]
            }
        }

        return if (map != null) map!![key] else null
    }

    /**
     * 根据键往表里插入值
     * @param key 键
     * @param value 值
     */
    fun put(key: Any?, value: Any?) {
        var key_: Any? = key ?: throw Exception("table index is nil!")
        if (key_ is Double && key_.isNaN()) {
            throw Exception("table index is NaN!")
        }

        key_ = floatToInt(key_)
        if (key_ is Long) {
            val idx = key_.toInt()
            if (idx >= 1) {
                if (arr == null) {
                    arr = ArrayList()
                }

                val arrLen = arr!!.size
                if (idx <= arrLen) {
                    arr!!.set(idx - 1, value)
                    if (idx == arrLen && value == null) {
                        shrinkArray()
                    }
                    return
                }
                if (idx == arrLen + 1) {
                    if (map != null) {
                        map!!.remove(key_)
                    }
                    if (value != null) {
                        arr!!.add(value)
                        expandArray()
                    }
                    return
                }
            }
        }

        if (value != null) {
            if (map == null) {
                map = HashMap()
            }
            map!![key_] = value
        } else {
            if (map != null) {
                map!!.remove(key_)
            }
        }
    }

    private fun shrinkArray() {
        for (i in arr!!.indices.reversed()) {
            if (arr!![i] == null) {
                arr!!.removeAt(i)
            }
        }
    }

    /**
     * 动态扩展数组
     */
    private fun expandArray() {
        if (map != null) {
            var idx = arr!!.size + 1
            while (true) {
                val value = map!!.remove(idx.toLong())
                if (value != null) {
                    arr!!.add(value)
                } else {
                    break
                }
                idx++
            }
        }
    }

    /**
     * 尝试把浮点型的键转换成整型
     * @param key 键
     * @return 整型值
     */
    private fun floatToInt(key: Any?): Any? {
        if (key is Double) {
            if (isInteger(key)) {
                return key.toLong()
            }
        }
        return key
    }

}
