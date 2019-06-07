package kotlua.test

import moe.slk.kotlua.binchunk.unDump
import org.junit.Assert
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths

class TestReader {
    @Test
    fun testUnDumpWithHelloWorldLua() {
        val data = Files.readAllBytes(
            Paths.get(
                this.javaClass.getResource("/hello_world.luac").toURI()
            )
        )
        val proto = unDump(data)

        with(proto) {
            Assert.assertEquals("@hello_world.lua", source)
            Assert.assertEquals(0, lineDefined)
            Assert.assertEquals(0, lastLineDefined)
            Assert.assertEquals(0, numParams.toInt())
            Assert.assertEquals(1, isVararg.toInt())
            Assert.assertEquals(2, maxStackSize.toInt())
            Assert.assertEquals(4, code.size)
            Assert.assertEquals(2, constants.size)
            Assert.assertEquals(1, upvalues.size)
            Assert.assertEquals(0, protos.size)
            Assert.assertEquals(4, lineInfo.size)
            Assert.assertEquals(0, localVars.size)
            Assert.assertEquals(1, upvalueNames.size)

            Assert.assertEquals("print", constants[0])
            Assert.assertEquals("Hello, World!", constants[1])
            Assert.assertEquals("_ENV", upvalueNames[0])
        }

    }

}