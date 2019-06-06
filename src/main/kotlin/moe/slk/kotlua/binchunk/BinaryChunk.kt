package moe.slk.kotlua.binchunk

data class BinaryChunk(
    val head: Header,
    val sizeUpvalues: Byte,
    val mainFunc: Prototype
)

