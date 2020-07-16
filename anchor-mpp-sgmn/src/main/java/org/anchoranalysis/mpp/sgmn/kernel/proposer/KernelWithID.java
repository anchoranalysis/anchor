/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.kernel.proposer;

import org.anchoranalysis.mpp.sgmn.bean.kernel.Kernel;

public class KernelWithID<T> {

    private Kernel<T> kernel;

    private int id;

    public KernelWithID(Kernel<T> kernel, int id) {
        this.kernel = kernel;
        this.id = id;
    }

    public String getDescription() {
        return kernel.dscrLast();
    }

    public Kernel<T> getKernel() {
        return kernel;
    }

    public void setKernel(Kernel<T> kernel) {
        this.kernel = kernel;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }
}
