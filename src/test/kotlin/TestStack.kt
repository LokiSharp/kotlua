package kotlua.test

import moe.slk.kotlua.state.LuaStack
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class LuaStackTest {

    private var stack: LuaStack? = null

    @Before
    fun initStack() {
        stack = LuaStack()
        stack!!.push(1)
        stack!!.push(true)
        stack!!.push("foo")
        assertEquals(3, stack!!.top)
    }

    @Test
    fun pop() {
        assertEquals("foo", stack!!.pop())
        assertEquals(true, stack!!.pop())
        assertEquals(1, stack!!.pop())
    }

    @Test
    fun get() {
        assertEquals(1, stack!!.get(1))
        assertEquals(1, stack!!.get(-3))
        assertEquals(true, stack!!.get(2))
        assertEquals(true, stack!!.get(-2))
        assertEquals("foo", stack!!.get(3))
        assertEquals("foo", stack!!.get(-1))
        assertEquals(null, stack!!.get(0))
        assertEquals(null, stack!!.get(4))
    }

    @Test
    fun set() {
        stack!!.set(2, "bar")
        assertEquals("bar", stack!!.get(2))
        stack!!.set(-1, 100)
        assertEquals(100, stack!!.get(3))
    }

}