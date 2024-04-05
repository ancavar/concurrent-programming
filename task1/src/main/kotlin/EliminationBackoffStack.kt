import java.util.concurrent.TimeoutException

class EliminationBackoffStack<T> : LockFreeStack<T>() {
    companion object {
        private const val SIZE = 100
    }

    private val eliminationArray = EliminationArray<T>(SIZE)

    private val policy = ThreadLocal.withInitial { RangePolicy() }

    override fun push(value: T) {
        val rangePolicy = policy.get()
        val node = Node(value)
        while (true) {
            if (tryPush(node)) {
                return
            } else {
                try {
                    val otherValue = eliminationArray.visit(value, rangePolicy.getRange())
                    if (otherValue == null) {
                        rangePolicy.recordEliminationSuccess()
                        return
                    }
                } catch (ex: TimeoutException) {
                    rangePolicy.recordEliminationTimeout()
                }
            }
        }
    }

    @Throws(EmptyException::class)
    override fun pop(): T {
        val rangePolicy = policy.get()
        while (true) {
            val returnNode = tryPop()
            if (returnNode != null) {
                return returnNode.value
            } else {
                try {
                    val otherValue = eliminationArray.visit(null, rangePolicy.getRange())
                    if (otherValue != null) {
                        rangePolicy.recordEliminationSuccess()
                        return otherValue
                    }
                } catch (ex: TimeoutException) {
                    rangePolicy.recordEliminationTimeout()
                }
            }
        }
    }

    private class RangePolicy {
        private var range: Int = INITIAL_RANGE

        companion object {
            private const val INITIAL_RANGE = 10
            private const val MIN_RANGE = 2
            private const val MAX_RANGE = 100
            private const val INCREASE_FACTOR = 1
            private const val DECREASE_FACTOR = 1
        }

        fun getRange(): Int {
            return range
        }

        fun recordEliminationSuccess() {
            range = Math.min(range + INCREASE_FACTOR, MAX_RANGE)
        }

        fun recordEliminationTimeout() {
            range = Math.max(range - DECREASE_FACTOR, MIN_RANGE)
        }
    }

}
