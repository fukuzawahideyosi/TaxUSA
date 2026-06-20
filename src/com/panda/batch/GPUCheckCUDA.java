package com.panda.batch;

import static jcuda.runtime.JCuda.*;

import jcuda.runtime.cudaDeviceProp;

public class GPUCheckCUDA {
    public static void main(String args[]) {
        // **检查 GPU 设备数量**
        int deviceCount[] = { 0 };
        cudaGetDeviceCount(deviceCount);
        System.out.println("检测到 GPU 设备数: " + deviceCount[0]);

        if (deviceCount[0] == 0) {
            System.out.println("❌ 没有找到可用的 CUDA 设备！");
            return;
        }

        // **打印 GPU 设备信息**
        for (int i = 0; i < deviceCount[0]; i++) {
            cudaDeviceProp prop = new cudaDeviceProp();
            cudaGetDeviceProperties(prop, i);
            System.out.println("\n🔹 GPU 设备 " + i + ": " + prop.name);
            System.out.println("   CUDA 核心数: " + prop.multiProcessorCount);
            System.out.println("   全局内存: " + (prop.totalGlobalMem / (1024 * 1024)) + " MB");
            System.out.println("   计算能力: " + prop.major + "." + prop.minor);
        }
    }
}
