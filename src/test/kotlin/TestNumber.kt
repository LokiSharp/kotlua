package kotlua.test

import moe.slk.kotlua.number.isInteger
import moe.slk.kotlua.number.parseFloat
import moe.slk.kotlua.number.parseInteger
import org.junit.Assert
import org.junit.Test

class TestNumber {
    @Test
    fun testIsInteger() {
        val result = isInteger(1.0)
        Assert.assertTrue(result)
    }

    @Test
    fun testIsNotInteger() {
        val result = isInteger(1.1)
        Assert.assertFalse(result)
    }

    @Test
    fun testCanParseInteger() {
        val result = parseInteger("100")
        Assert.assertNotNull(result)
    }

    @Test
    fun testCanNotParseInteger() {
        val result = parseInteger("It's not Integer")
        Assert.assertNull(result)
    }

    @Test
    fun testCanParseFloat() {
        val result = parseFloat("10.0")
        Assert.assertNotNull(result)
    }

    @Test
    fun testCanNotParseFloat() {
        val result = parseFloat("It's not Float")
        Assert.assertNull(result)
    }
}