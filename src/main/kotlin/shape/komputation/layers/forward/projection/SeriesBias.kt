package shape.komputation.layers.forward.projection

import shape.komputation.functions.add
import shape.komputation.functions.backwardProjectionWrtBias
import shape.komputation.initialization.InitializationStrategy
import shape.komputation.initialization.initializeColumnVector
import shape.komputation.matrix.DoubleMatrix
import shape.komputation.optimization.DenseAccumulator
import shape.komputation.optimization.OptimizationStrategy
import shape.komputation.optimization.UpdateRule
import shape.komputation.optimization.updateDensely

class SeriesBias internal constructor(
    private val name : String?,
    private val bias: DoubleArray,
    private val seriesAccumulator: DenseAccumulator,
    private val batchAccumulator: DenseAccumulator,
    private val updateRule: UpdateRule? = null) {

    private val numberBiasEntries = bias.size

    fun forwardStep(input : DoubleMatrix) =

        DoubleMatrix(input.numberRows, input.numberColumns, add(input.entries, bias))

    fun backwardStep(chain: DoubleMatrix) {

        val backwardWrtBias = backwardProjectionWrtBias(this.bias.size, chain.entries, chain.numberRows, chain.numberColumns)

        this.seriesAccumulator.accumulate(backwardWrtBias)

    }

    fun backwardSeries() {

        val seriesAccumulator = this.seriesAccumulator

        this.batchAccumulator.accumulate(seriesAccumulator.getAccumulation())

        seriesAccumulator.reset()

    }

    fun optimize(scalingFactor : Double) {

        val batchAccumulator = this.batchAccumulator

        if (this.updateRule != null) {

            updateDensely(this.bias, batchAccumulator.getAccumulation(), scalingFactor, updateRule)

        }

        batchAccumulator.reset()

    }

}

fun seriesBias(
    dimension: Int,
    initializationStrategy: InitializationStrategy,
    optimizationStrategy: OptimizationStrategy?) =

    seriesBias(null, dimension, initializationStrategy, optimizationStrategy)

fun seriesBias(
    name : String?,
    dimension: Int,
    initializationStrategy: InitializationStrategy,
    optimizationStrategy: OptimizationStrategy?) : SeriesBias {

    val bias = initializeColumnVector(initializationStrategy, dimension)

    val seriesAccumulator = DenseAccumulator(dimension)
    val batchAccumulator = DenseAccumulator(dimension)

    val updateRule = optimizationStrategy?.invoke(dimension, 1)

    return SeriesBias(name, bias, seriesAccumulator, batchAccumulator, updateRule)

}