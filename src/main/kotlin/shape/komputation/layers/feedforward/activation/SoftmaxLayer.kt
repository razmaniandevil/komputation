package shape.komputation.layers.feedforward.activation

import shape.komputation.functions.activation.backwardColumnWiseSoftmax
import shape.komputation.functions.activation.columnWiseSoftmax
import shape.komputation.matrix.DoubleMatrix

class SoftmaxLayer(name : String? = null) : ActivationLayer(name) {

    private var forwardResult : DoubleMatrix? = null

    override fun forward(input : DoubleMatrix) : DoubleMatrix {

        val numberRows = input.numberRows
        val numberColumns = input.numberColumns

        this.forwardResult = DoubleMatrix(numberRows, numberColumns, columnWiseSoftmax(input.entries, numberRows, numberColumns))

        return this.forwardResult!!

    }

    /*
        Note that each pre-activation effects all nodes.
        For i == j: prediction (1 - prediction)
        for i != j: -(prediction_i * prediction_j)
     */
    override fun backward(chain : DoubleMatrix): DoubleMatrix {

        val forwardResult = this.forwardResult!!
        val forwardEntries = forwardResult.entries
        val numberForwardRows = forwardResult.numberRows
        val numberForwardColumns = forwardResult.numberColumns

        val chainEntries = chain.entries

        val gradient = backwardColumnWiseSoftmax(numberForwardRows, numberForwardColumns, forwardEntries, chainEntries)

        return DoubleMatrix(numberForwardRows, numberForwardColumns, gradient)

    }

}