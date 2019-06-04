package moe.slk.kotlua.binchunk.types

data class BinaryChunk(
    val head: Header,
    val sizeUpvalues: Byte,
    val mainFunc: Prototype
)

