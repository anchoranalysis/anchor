/*-
 * #%L
 * anchor-core
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

package org.anchoranalysis.core.progress;

/**
 * Combines a number of sub progress reporters.
 * 
 * @author Owen Feehan
 *
 */
public class ProgressMultiple implements AutoCloseable {

    private Progress parent;
    private double part = 0.0;
    private double cumulativePart = 0.0;
    private int index = 0;

    public ProgressMultiple(Progress parent, int numberChildren) {
        this.parent = parent;
        this.part = 100.0 / numberChildren;
        parent.setMin(0);
        parent.setMax(100);
    }

    public void incrementWorker() {
        index++;
        this.cumulativePart = part * index;
        parent.update((int) Math.floor(cumulativePart));
    }

    // Progress for the current worker
    public void update(double progress) {
        parent.update((int) Math.floor(cumulativePart + (progress * part / 100)));
    }

    @Override
    public void close() {
        parent.update(100);
    }
}
