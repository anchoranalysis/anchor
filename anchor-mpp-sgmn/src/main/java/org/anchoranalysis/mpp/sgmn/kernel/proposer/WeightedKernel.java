/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.kernel.proposer;

import org.anchoranalysis.mpp.sgmn.bean.kernel.Kernel;

/**
 * A kernel with an associated weight.
 *
 * @author Owen Feehan
 * @param <T> type being modified by the kernel.
 */
public class WeightedKernel<T> {

    private Kernel<T> kernel = null;

    private double weight = 0;

    private String name;

    /**
     * Constructor for single kernel factory
     *
     * @param kernel the kernel
     * @param weight an associated weight (a positive floating-point number)
     */
    public WeightedKernel(Kernel<T> kernel, double weight) {
        this.setKernel(kernel);
        this.weight = weight;
        this.name = kernel.getBeanName();
    }

    public double getWeight() {
        return this.weight;
    }

    @Override
    public String toString() {
        return String.format("weight=%f, factory=%s", this.weight, this.kernel.toString());
    }

    public Kernel<T> getKernel() {
        return kernel;
    }

    public void setKernel(Kernel<T> kernel) {
        this.kernel = kernel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
