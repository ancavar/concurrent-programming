open class LockFreeStack<T>: Stack<T>() {
    protected fun tryPush(node: Node<T>) : Boolean {
        val oldTop = top.get()
        node.next = oldTop
        return top.compareAndSet(oldTop, node)
    }

    override fun push(value: T) {
        val node = Node(value)
        while (true) {
            if (tryPush(node)) return
        }
    }

    protected fun tryPop() : Node<T>? {
        val oldTop = top.get() ?: throw EmptyException()
        val newTop = oldTop.next
        if (top.compareAndSet(oldTop, newTop)) {
            return oldTop
        }
        return null
    }

    override fun pop(): T {
        while (true) {
            val popNode = tryPop()
            if (popNode != null) {
                return popNode.value
            }
        }
    }
}



