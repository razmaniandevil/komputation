#include "zero/Zero.cuh"

extern "C"
__global__ void backwardDropoutKernel (int batchSize, int numberEntriesPerInstance, int numberIterations, float* chain, float* mask, float* result)
{

    // What's the first entry index within the instance that this thread should operate on?
    int startIndexWithinInstance = blockIdx.y * (blockDim.x * numberIterations) + threadIdx.x * numberIterations;

    // Continue if this index is smaller than the dimension of the instance.
    if(startIndexWithinInstance < numberEntriesPerInstance) {

        // What's the first entry index within the batch that this thread should operate on?
        int startIndexWithinBatch = blockIdx.x * numberEntriesPerInstance + startIndexWithinInstance;

        // Is the instance greater than the current batch size?
        if(blockIdx.x >= batchSize) {

            setToZero(result, startIndexWithinBatch, numberIterations);

        }
        else {

            for(int indexEntry = startIndexWithinBatch; indexEntry < startIndexWithinBatch + numberIterations; indexEntry++) {

                result[indexEntry] = chain[indexEntry] * mask[indexEntry];

            }

        }

    }

}