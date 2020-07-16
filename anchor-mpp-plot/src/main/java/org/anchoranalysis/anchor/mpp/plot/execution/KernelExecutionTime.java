/*-
 * #%L
 * anchor-mpp-plot
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
