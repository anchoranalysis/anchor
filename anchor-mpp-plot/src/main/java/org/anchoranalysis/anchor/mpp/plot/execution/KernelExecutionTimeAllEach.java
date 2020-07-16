/* (C)2020 */
package org.anchoranalysis.anchor.mpp.plot.execution;

// Stores a KernelExecutionTime for all kernels, and each kernel separately
public class KernelExecutionTimeAllEach {

    private KernelExecutionTime all = null;
    private KernelExecutionTime[] each;

    public KernelExecutionTimeAllEach(int numKernels) {
        each = new KernelExecutionTime[numKernels];
    }

    public KernelExecutionTime getAll() {
        return all;
    }

    public void setAll(KernelExecutionTime all) {
        this.all = all;
    }

    public KernelExecutionTime getKernel(int index) {
        return each[index];
    }

    public void setKernel(int index, KernelExecutionTime ket) {
        each[index] = ket;
    }

    public KernelExecutionTime[] getKernelArray() {
        return each;
    }

    // -1 indicates all, anything else is an ID for each
    public KernelExecutionTime getForKernelID(int id) {
        if (id == -1) {
            return all;
        } else {
            return each[id];
        }
    }
}
