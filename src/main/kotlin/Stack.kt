import java.util.concurrent.atomic.AtomicReference

abstract class Stack<T> {
    val top = AtomicReference<Node<T>?>(null)
    abstract fun push(value: T)
    abstract fun pop(): T
    fun peek(): T {
        val currentTop = top.get() ?: throw EmptyException()
        return currentTop.value
    }

    protected class EmptyException : Exception()
}