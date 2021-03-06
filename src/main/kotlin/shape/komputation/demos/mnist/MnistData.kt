package shape.komputation.demos.mnist

import shape.komputation.matrix.*
import java.io.File

object MnistData {

    val numberCategories = 10

    private fun loadMnist(csvFile: File, size: Int): Pair<Array<Matrix>, Array<FloatMatrix>> {

        val inputs = Array<Matrix>(size) { EMPTY_FLOAT_MATRIX }
        val targets = Array(size) { EMPTY_FLOAT_MATRIX }

        csvFile
            .bufferedReader()
            .lineSequence()
            .forEachIndexed { index, line ->

                val split = line
                    .split(",")

                val category = split.first().toInt()

                val target = oneHotVector(numberCategories, category)
                val input = floatColumnVector(*split.drop(1).map { it.toFloat().div(255.0f) }.toFloatArray())

                targets[index] = target
                inputs[index] = input

            }

        return inputs to targets

    }

    val numberTrainingExamples = 60_000
    val numberTestExamples = 10_000

    fun loadMnistTraining(csvFile: File) =

        loadMnist(csvFile, this.numberTrainingExamples)

    fun loadMnistTest(csvFile: File) =

        loadMnist(csvFile, this.numberTestExamples)

}