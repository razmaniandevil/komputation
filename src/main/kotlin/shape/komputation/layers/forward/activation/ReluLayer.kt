package shape.komputation.layers.forward.activation

import jcuda.jcublas.cublasHandle
import shape.komputation.cpu.layers.forward.activation.CpuReluLayer
import shape.komputation.cuda.CudaContext
import shape.komputation.cuda.layers.forward.activation.CudaReluLayer
import shape.komputation.layers.CpuActivationLayerInstruction
import shape.komputation.layers.CudaActivationLayerInstruction

class ReluLayer(private val name : String?, private val numberEntries : Int) : CpuActivationLayerInstruction, CudaActivationLayerInstruction {

    override fun buildForCpu() =

        CpuReluLayer(this.name, this.numberEntries)

    override fun buildForCuda(context : CudaContext, cublasHandle: cublasHandle): CudaReluLayer {

        val kernelFactory = context.kernelFactory

        return CudaReluLayer(
            this.name,
            this.numberEntries,
            { kernelFactory.relu() },
            { kernelFactory.backwardRelu() },
            context.numberMultiprocessors,
            context.maximumNumberOfResidentWarpsPerMultiprocessor,
            context.warpSize,
            context.maximumNumberOfThreadsPerBlock)

    }

}

fun reluLayer(numberEntries : Int) = reluLayer(null, numberEntries)

fun reluLayer(name : String? = null, numberEntries : Int) = ReluLayer(name, numberEntries)