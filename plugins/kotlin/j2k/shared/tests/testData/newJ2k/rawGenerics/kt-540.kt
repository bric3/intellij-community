package demo

internal class Test {
    fun main() {
        val common: List<String> = ArrayList()
        val raw: List<*> = ArrayList<String>()
        val superRaw: List<*> = ArrayList<Any?>()
    }
}
