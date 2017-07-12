package shape.komputation.functions

import jcuda.Pointer
import jcuda.Sizeof
import jcuda.jcublas.JCublas2.*
import jcuda.jcublas.cublasHandle
import jcuda.jcublas.cublasOperation.CUBLAS_OP_N
import jcuda.jcublas.cublasOperation.CUBLAS_OP_T

fun cublasProject(cublasHandle: cublasHandle, deviceInput: Pointer, deviceResult : Pointer, deviceWeights: Pointer, numberWeightRows: Int, numberWeightColumns: Int, deviceBias: Pointer? = null, biasDimension : Int = 0): DoubleArray {

    if (deviceBias != null) {

        cublasDcopy(cublasHandle, biasDimension, deviceBias, 1, deviceResult, 1)

    }

    // C = alpha * op(A) * op(B) + beta * C
    val beta = if (deviceBias != null) 1.0 else 0.0
    cublasDgemv(
        cublasHandle,
        CUBLAS_OP_N, // no transposition
        numberWeightRows, // number of rows of matrix A
        numberWeightColumns, // number of columns of matrix A
        Pointer.to(doubleArrayOf(1.0)), // alpha
        deviceWeights, // weight pointer
        numberWeightRows, // number weight rows
        deviceInput, // input pointer
        1, // storage spacing between elements of x
        Pointer.to(doubleArrayOf(beta)), // beta
        deviceResult, // result pointer
        numberWeightRows // number result rows
    )

    val hostResult = DoubleArray(numberWeightRows)
    cublasGetVector(numberWeightRows, Sizeof.DOUBLE, deviceResult, 1, Pointer.to(hostResult), 1)

    return hostResult

}

/*
    Differentiation w.r.t input:

    d Wx / d x = w_11 + w_21
                 w_12 + w_22
                 w_13 + w_23

    gemv solution:
                              chain_1
                              chain_2
    transposed W >> w_11 w_21
                    w_12 w_22
                    w_13 w_23
 */
fun cublasBackwardProjectionWrtInput(cublasHandle: cublasHandle, deviceWeights: Pointer, numberWeightRows: Int, numberWeightColumns: Int, deviceChain: Pointer, deviceResult : Pointer) {

    cublasDgemv(
        cublasHandle,
        CUBLAS_OP_T, // transpose
        numberWeightRows, // number of rows of matrix A
        numberWeightColumns, // number of columns of matrix A
        Pointer.to(doubleArrayOf(1.0)), // alpha
        deviceWeights, // weight pointer
        numberWeightRows, // number weight rows
        deviceChain, // input pointer
        1, // storage spacing between elements of x
        Pointer.to(doubleArrayOf(0.0)), // beta
        deviceResult, // result pointer
        1 // specifies the storage spacing between elements of y
    )

}

/*
    Differentiation w.r.t weights:

    d Wx / d W = x_1 x_2 x_3
                 x_1 x_2 x_3

    ger solution:
            x1 x2 x3 << transposed x
    chain_1
    chain_2
 */
fun cublasBackwardProjectionWrtWeights(cublasHandle: cublasHandle, deviceInput: Pointer, deviceChain: Pointer, deviceAccumulator: Pointer, numberWeightRows: Int, numberWeightColumns : Int) {

    cublasDger(
        cublasHandle,
        numberWeightRows, // rows of matrix A
        numberWeightColumns, // columns of matrix A
        Pointer.to(doubleArrayOf(1.0)), // alpha
        deviceChain,
        1,
        deviceInput,
        1,
        deviceAccumulator,
        numberWeightRows // rows of matrix A
    )

}

fun cublasBackwardProjectionWrtBias(cublasHandle: cublasHandle, deviceChain: Pointer, chainDimension : Int, deviceAccumulator: Pointer) {

    cublasDgeam(
        cublasHandle,
        CUBLAS_OP_N,
        CUBLAS_OP_N,
        chainDimension,
        1,
        Pointer.to(doubleArrayOf(1.0)),
        deviceChain,
        chainDimension,
        Pointer.to(doubleArrayOf(1.0)),
        deviceAccumulator,
        chainDimension,
        deviceAccumulator,
        chainDimension
    )


}