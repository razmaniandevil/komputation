package shape.komputation.cuda.layers.forward.activation

import jcuda.jcublas.cublasHandle
import org.junit.jupiter.api.Test
import shape.komputation.cpu.functions.activation.relu
import shape.komputation.cuda.CudaContext
import shape.komputation.layers.forward.activation.reluLayer

class CudaReluLayerTest : BaseCudaEntrywiseActivationLayerTest() {

    override fun createLayer(context: CudaContext, numberEntries: Int) =

        reluLayer(numberEntries).buildForCuda(context, cublasHandle())

    @Test
    fun testForwardOneOfTwoInstancesOneDimensional() {

        val input = floatArrayOf(1.0f, 0.0f)
        val expected = floatArrayOf(relu(1.0f), 0.0f)

        testForward(input, 1, 2, expected)

    }

    @Test
    fun testForwardOneOfTwoInstancesTwoDimensional() {

        val input = floatArrayOf(1.0f, 2.0f, 0.0f, 0.0f)
        val expected = floatArrayOf(relu(1.0f), relu(2.0f), 0.0f, 0.0f)

        testForward(input, 1, 2, expected)

    }

    @Test
    fun testBackwardOneDimension1() {

        val input = floatArrayOf(1.0f, 0.0f)
        val chain = floatArrayOf(2.0f, 0.0f)
        val expected = floatArrayOf(2.0f, 0.0f)

        testBackward(input, chain, 1, 2, expected)

    }

    @Test
    fun testBackwardOneDimension2() {

        val input = floatArrayOf(-1.0f)
        val chain = floatArrayOf(2.0f)
        val expected = floatArrayOf(0.0f)

        testBackward(input, chain, 1, 1, expected)

    }

    @Test
    fun testBackwardOneDimension3() {

        val input = floatArrayOf(0.0f)
        val chain = floatArrayOf(2.0f)
        val expected = floatArrayOf(0.0f)

        testBackward(input, chain, 1, 1, expected)

    }


    @Test
    fun testBackwardTwoDimensions() {

        val input = floatArrayOf(-1.0f, 1.0f, 0.0f, 0.0f)
        val chain = floatArrayOf(1.0f, 2.0f, 0.0f, 0.0f)
        val expected = floatArrayOf(0.0f, 2.0f, 0.0f, 0.0f)

        testBackward(input, chain, 1, 2, expected)

    }


}