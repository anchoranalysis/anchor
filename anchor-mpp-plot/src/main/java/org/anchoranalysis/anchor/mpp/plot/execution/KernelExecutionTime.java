/* (C)2020 */
package org.anchoranalysis.anchor.mpp.plot.execution;

import org.anchoranalysis.core.arithmetic.DoubleUtilities;

public class KernelExecutionTime {

    private double executionTime;
    private double acceptedTime;
    private double rejectedTime;
    private double proposedTime;
    private double notProposedTime;

    private long executionCnt;
    private long acceptedCnt;
    private long rejectedCnt;
    private long proposedCnt;
    private long notProposedCnt;

    private String kernelName;

    public KernelExecutionTime(String kernelName) {
        super();
        this.kernelName = kernelName;
    }

    public double meanExecutionTime() {
        return DoubleUtilities.divideByZeroReplace(getExecutionTime(), getExecutionCnt(), 0);
    }

    public double getExecutionTime() {
        return executionTime;
    }

    public double getAcceptedTime() {
        return acceptedTime;
    }

    public void setAcceptedTime(double acceptedTime) {
        this.acceptedTime = acceptedTime;
    }

    public double getRejectedTime() {
        return rejectedTime;
    }

    public void setRejectedTime(double rejectedTime) {
        this.rejectedTime = rejectedTime;
    }

    public double getProposedTime() {
        return proposedTime;
    }

    public void setProposedTime(double proposedTime) {
        this.proposedTime = proposedTime;
    }

    public double getNotProposedTime() {
        return notProposedTime;
    }

    public void setNotProposedTime(double notProposedTime) {
        this.notProposedTime = notProposedTime;
    }

    public void setExecutionTime(double executionTime) {
        this.executionTime = executionTime;
    }

    public long getExecutionCnt() {
        return executionCnt;
    }

    public void setExecutionCnt(long executedCnt) {
        this.executionCnt = executedCnt;
    }

    public long getAcceptedCnt() {
        return acceptedCnt;
    }

    public void setAcceptedCnt(long acceptedCnt) {
        this.acceptedCnt = acceptedCnt;
    }

    public long getRejectedCnt() {
        return rejectedCnt;
    }

    public void setRejectedCnt(long rejectedCnt) {
        this.rejectedCnt = rejectedCnt;
    }

    public long getProposedCnt() {
        return proposedCnt;
    }

    public void setProposedCnt(long proposedCnt) {
        this.proposedCnt = proposedCnt;
    }

    public long getNotProposedCnt() {
        return notProposedCnt;
    }

    public void setNotProposedCnt(long notProposedCnt) {
        this.notProposedCnt = notProposedCnt;
    }

    public String getKernelName() {
        return kernelName;
    }
}
