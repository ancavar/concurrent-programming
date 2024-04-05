package benchmark

import EliminationBackoffStack
import LockFreeStack
import kotlinx.benchmark.*
import org.openjdk.jmh.annotations.Threads
import kotlin.random.Random

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = BenchmarkTimeUnit.MILLISECONDS)
@Measurement(iterations = 30, time = 2, timeUnit = BenchmarkTimeUnit.SECONDS)
@State(Scope.Benchmark)
class LockFreeBenchmark {
    private lateinit var stack: LockFreeStack<Int>
    private lateinit var eliminationBackoffStack: EliminationBackoffStack<Int>

    @Setup
    fun prepare() {
        stack = LockFreeStack()
        eliminationBackoffStack = EliminationBackoffStack()
        for (i in 0..< 1_000_000) {
            stack.push(i)
            eliminationBackoffStack.push(i)
        }
    }

    @Benchmark
    fun benchmarkLockFreeMethod() {
        if (Random.nextInt(2) == 1) {
            stack.push(52)
        } else {
            stack.pop()
        }
    }

    @Benchmark
    fun benchmarkEliminationMethod() {
        if (Random.nextInt(2) == 1) {
            eliminationBackoffStack.push(52)
        } else {
            eliminationBackoffStack.pop()
        }
    }

}