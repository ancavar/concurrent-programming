import org.example.CoarseGrainedTree
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.junit.jupiter.api.Test

class LincheckTest {
    private val bst = CoarseGrainedTree<Int, String>()

    @Operation
    fun insert(key: Int, value: String) = bst.insert(key, value)

    @Operation
    fun search(key: Int) = bst.search(key)

    @Operation
    fun delete(key: Int) = bst.delete(key)

//    @Test
//    fun modelTest() =
//        ModelCheckingOptions()
//            .threads(3)
//            .actorsPerThread(3)
//            .actorsAfter(1)
//            .iterations(50)
//            .invocationsPerIteration(1000)
//            .checkObstructionFreedom()
//            .check(this::class.java)

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