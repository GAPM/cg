package grpc.lib.symbol

object CastTable {
    private val numeric = listOf(
            Type.int8, Type.int16, Type.int32, Type.int64,
            Type.uint8, Type.uint16, Type.uint32, Type.uint64,
            Type.float, Type.double
    )

    private val tab = mapOf(
            Type.int8 to numeric,
            Type.int16 to numeric,
            Type.int32 to numeric,
            Type.int64 to numeric,
            Type.uint8 to numeric,
            Type.uint16 to numeric,
            Type.uint32 to numeric,
            Type.uint64 to numeric,
            Type.float to numeric,
            Type.double to numeric,
            Type.bool to numeric,
            Type.char to listOf(Type.string),
            Type.string to listOf()
    )

    fun check(type1: Type, type2: Type): Boolean {
        if (tab.containsKey(type1)) {
            return tab[type1]!!.contains(type2)
        } else {
            return false
        }
    }
}