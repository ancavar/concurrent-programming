import java.util.concurrent.atomic.AtomicStampedReference
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class LockFreeExchanger<T> {
    companion object {
        private const val EMPTY = 0
        private const val WAITING = 1
        private const val BUSY = 2
    }

    private val slot = AtomicStampedReference<T>(null, EMPTY)

    fun exchange(myItem: T?, timeout: Long, unit: TimeUnit): T {
        val nanos = unit.toNanos(timeout)
        val timeBound = System.nanoTime() + nanos
        val stampHolder = intArrayOf(EMPTY)

        while (true) {
            if (System.nanoTime() > timeBound) {
                throw TimeoutException()
            }

            val currentItem = slot.get(stampHolder)
            val currentStamp = stampHolder[0]

            when (currentStamp) {
                EMPTY -> {
                    if (slot.compareAndSet(currentItem, myItem, EMPTY, WAITING)) {
                        while (System.nanoTime() < timeBound) {
                            val otherItem = slot.get(stampHolder)
                            if (stampHolder[0] == BUSY) {
                                slot.set(null, EMPTY)
                                return otherItem
                            }
                        }
                        if (slot.compareAndSet(myItem, null, WAITING, EMPTY)) {
                            throw TimeoutException()
                        } else {
                            val otherItem = slot.get(stampHolder)
                            slot.set(null, EMPTY)
                            return otherItem
                        }
                    }
                }
                WAITING -> {
                    if (slot.compareAndSet(currentItem, myItem, WAITING, BUSY)) {
                        return currentItem
                    }
                }
                BUSY -> {
                }
                else -> {
                    throw IllegalStateException("Unexpected stamp value")
                }
            }
        }
    }
}

