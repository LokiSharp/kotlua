package kotlua.test

import moe.slk.kotlua.api.ArithOp
import moe.slk.kotlua.api.ArithOp.*
import moe.slk.kotlua.api.LuaState
import moe.slk.kotlua.binchunk.Prototype
import moe.slk.kotlua.state.LuaStateImpl
import org.junit.Assert
import org.junit.Test
import java.lang.Double.NEGATIVE_INFINITY
import java.lang.Double.POSITIVE_INFINITY


class LuaStateArithTest {

    private val proto = Prototype()

    @Test
    fun idiv() {
        Assert.assertEquals(1L, calc(5L, 3L, LUA_OPIDIV))
        Assert.assertEquals(-2L, calc(-5L, 3L, LUA_OPIDIV))
        Assert.assertEquals(-2.0, calc(5L, -3.0, LUA_OPIDIV))
        Assert.assertEquals(1.0, calc(-5.0, -3.0, LUA_OPIDIV))
    }

    @Test
    fun mod() {
        Assert.assertEquals(2L, calc(5L, 3L, LUA_OPMOD))
        Assert.assertEquals(1L, calc(-5L, 3L, LUA_OPMOD))
        Assert.assertEquals(-1.0, calc(5L, -3.0, LUA_OPMOD))
        Assert.assertEquals(-2.0, calc(-5.0, -3.0, LUA_OPMOD))

        Assert.assertEquals(2.0, calc(2.0, POSITIVE_INFINITY, LUA_OPMOD))
        Assert.assertEquals(POSITIVE_INFINITY, calc(-2.0, POSITIVE_INFINITY, LUA_OPMOD))
        Assert.assertEquals(-2.0, calc(-2.0, NEGATIVE_INFINITY, LUA_OPMOD))
        Assert.assertEquals(NEGATIVE_INFINITY, calc(2.0, NEGATIVE_INFINITY, LUA_OPMOD))
    }

    @Test
    fun shift() {
        Assert.assertEquals(0b1100L, calc(0b0110L, 1L, LUA_OPSHL))
        Assert.assertEquals(0b1100L, calc(0b0110L, -1L, LUA_OPSHR))
        Assert.assertEquals(0b0011L, calc(0b0110L, -1L, LUA_OPSHL))
        Assert.assertEquals(0b0011L, calc(0b0110L, 1L, LUA_OPSHR))
    }

    private fun calc(x: Any, y: Any, op: ArithOp): Any {
        val ls = LuaStateImpl(proto)
        pushOperand(ls, x)
        pushOperand(ls, y)
        ls.arith(op)
        return getResult(ls)
    }

    private fun pushOperand(ls: LuaState, x: Any) {
        if (x is Long) {
            ls.pushInteger(x)
        } else {
            ls.pushNumber(x as Double)
        }
    }

    private fun getResult(ls: LuaState): Any {
        val i = ls.toIntegerX(1)
        return i ?: ls.toNumber(1)
    }

}