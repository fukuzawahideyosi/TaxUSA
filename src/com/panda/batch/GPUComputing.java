package com.panda.batch;

import static jcuda.runtime.cudaMemcpyKind.*;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas;
import jcuda.runtime.JCuda;

public class GPUComputing {
    public static void main(String args[]) {
        try {
            // **初始化 JCublas**
            JCublas.setExceptionsEnabled(true);
            JCublas.cublasInit();

            int matrixSize = 2048; // 矩阵大小 2048x2048
            int numElements = matrixSize * matrixSize;
            int size = numElements * Sizeof.FLOAT;

            // **创建 CPU 端数据**
            float hostA[] = new float[numElements];
            float hostB[] = new float[numElements];
            float hostC[] = new float[numElements];

            for (int i = 0; i < numElements; i++) {
                hostA[i] = i;
                hostB[i] = i;
            }

            // **分配 GPU 端内存**
            Pointer d_A = new Pointer();
            Pointer d_B = new Pointer();
            Pointer d_C = new Pointer();

            JCuda.cudaMalloc(d_A, size);
            JCuda.cudaMalloc(d_B, size);
            JCuda.cudaMalloc(d_C, size);

            // **复制数据到 GPU**
            JCuda.cudaMemcpy(d_A, Pointer.to(hostA), size, cudaMemcpyHostToDevice);
            JCuda.cudaMemcpy(d_B, Pointer.to(hostB), size, cudaMemcpyHostToDevice);

            // **记录计算时间**
            long startTime = System.nanoTime();
            long duration = 60 * 1000000000L; // 60 秒
            int iterations = 0;

            while (System.nanoTime() - startTime < duration) {
                // **执行 GPU 矩阵乘法 C = A * B**
                JCublas.cublasSgemm('N', 'N', matrixSize, matrixSize, matrixSize,
                        1.0f, d_A, matrixSize, d_B, matrixSize, 0.0f, d_C, matrixSize);
                iterations++;
            }

            // **计算结束**
            long endTime = System.nanoTime();
            double seconds = (endTime - startTime) / 1.0e9;
            System.out.println("🚀 GPU 计算执行 " + seconds + " 秒，共执行 " + iterations + " 次");

            // **释放 GPU 资源**
            JCuda.cudaFree(d_A);
            JCuda.cudaFree(d_B);
            JCuda.cudaFree(d_C);
            JCublas.cublasShutdown();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
