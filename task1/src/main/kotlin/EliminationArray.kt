import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class EliminationArray<T>(capacity: Int) {
    companion object {
        private const val duration = 100L
    }

    private val exchanger: Array<LockFreeExchanger<T>> = Array(capacity) { LockFreeExchanger() }

    @Throws(TimeoutException::class)
    fun visit(value: T?, range: Int): T {
        val slot = ThreadLocalRandom.current().nextInt(range)
        return exchanger[slot].exchange(value, duration, TimeUnit.MICROSECONDS)
    }
}

