import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

class EliminationBackoffStackTest {
    private val lockFreeStack = LockFreeStack<Int>()

    @Operation
    fun push(value: Int) = lockFreeStack.push(value)

    @Operation
    fun pop() = lockFreeStack.pop()

    @Operation
    fun peek() = lockFreeStack.peek()

    @Test
    fun pushTest() {
        lockFreeStack.push(2)
        assert (lockFreeStack.top.get()?.value == 2)
    }

    @Test
    fun popTest() {
        lockFreeStack.push(3)
        assert (lockFreeStack.pop() == 3)
    }

    @Test
    fun peekTest() {
        lockFreeStack.push(5)
        assert (lockFreeStack.peek() == 5)
    }


    @Test
    fun modelTest() =
        ModelCheckingOptions()
            .threads(3)
            .actorsPerThread(3)
            .actorsAfter(1)
            .iterations(50)
            .invocationsPerIteration(1000)
            .checkObstructionFreedom()
            .check(this::class.java)

    @Test
    fun stressTest() =
        StressOptions()
            .threads(3)
            .actorsPerThread(3)
            .actorsAfter(1)
            .iterations(50)
            .invocationsPerIteration(1000)
            .check(this::class.java)
}