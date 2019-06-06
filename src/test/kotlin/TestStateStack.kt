package kotlua.test

import moe.slk.kotlua.api.LuaState
import moe.slk.kotlua.binchunk.Prototype
import moe.slk.kotlua.state.LuaStateImpl
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LuaStateStackTest {

    private var ls: LuaState? = null
    private val proto = Prototype()

    private fun lsToString(): String {
        val sb = StringBuilder()
        for (i in 1..ls!!.top) {
            sb.append(ls!!.toInteger(i))
        }
        return sb.toString()
    }

    @Before
    fun initLuaState() {
        ls = LuaStateImpl(proto)
        for (i in 1..9) {
            ls!!.pushInteger(i.toLong())
        }
        assertEquals("123456789", lsToString())
    }

    @Test
    fun stack() {
        ls!!.copy(8, 3)
        assertEquals("128456789", lsToString())
        ls!!.pushValue(5)
        assertEquals("1284567895", lsToString())
        ls!!.replace(1)
        assertEquals("528456789", lsToString())
        ls!!.insert(2)
        assertEquals("592845678", lsToString())
        ls!!.rotate(5, 1)
        assertEquals("592884567", lsToString())
        ls!!.pop(2)
        assertEquals("5928845", lsToString())
        ls!!.top = 5
        assertEquals("59288", lsToString())
    }
}