package shape.komputation.layers.forward.activation

import jcuda.jcublas.cublasHandle
import shape.komputation.cpu.layers.forward.activation.CpuSoftmaxLayer
import shape.komputation.cuda.CudaContext
import shape.komputation.cuda.layers.forward.activation.CudaSoftmaxLayer
import shape.komputation.layers.CpuActivationLayerInstruction
import shape.komputation.layers.CudaActivationLayerInstruction
import shape.komputation.layers.concatenateNames
import shape.komputation.layers.forward.normalizationLayer

class SoftmaxLayer(private val name : String?, private val numberRows : Int, private val numberColumns : Int) : CpuActivationLayerInstruction, CudaActivationLayerInstruction {

    private val exponentiationLayer = exponentiationLayer(concatenateNames(this.name, "exponentiation"), this.numberRows * this.numberColumns)
    private val normalizationLayer = normalizationLayer(concatenateNames(this.name, "normalization"), this.numberRows, this.numberColumns)

    override fun buildForCpu() =

        CpuSoftmaxLayer(this.name, this.exponentiationLayer.buildForCpu(), this.normalizationLayer.buildForCpu())

    override fun buildForCuda(context: CudaContext, cublasHandle: cublasHandle) =

        CudaSoftmaxLayer(this.name, this.exponentiationLayer.buildForCuda(context, cublasHandle), this.normalizationLayer.buildForCuda(context, cublasHandle))

}


fun softmaxLayer(numberCategories: Int, numberSteps: Int = 1) =

    SoftmaxLayer(null, numberCategories, numberSteps)

fun softmaxLayer(name : String? = null, numberCategories: Int, numberSteps: Int = 1) =

    SoftmaxLayer(name, numberCategories, numberSteps)