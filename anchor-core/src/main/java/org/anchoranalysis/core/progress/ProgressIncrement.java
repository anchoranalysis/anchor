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

import lombok.RequiredArgsConstructor;

/**
 * Like {@link Progress} but rather than explicitly setting a value for progress, the value
 * increments with the {@link #update} operation.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class ProgressIncrement implements AutoCloseable {

    // START REQUIRED ARGUMENTS
    /** An underlying {@code Progress} that is incremented with each call to {@link #update}. */
    private final Progress progress;
    // END REQUIRED ARGUMENTS

    private int count = 0;

    /** Begins tracking progress, to be called before any calls to {@link #update}. */
    public void open() {
        count = progress.getMin();
        progress.open();
    }

    /**
     * Informs the tracker of an update in the progress.
     *
     * <p>Internally, this increments the progress tracker.
     */
    public void update() {
        progress.update(++count);
    }

    /** Ends tracking progress, to be called after any calls to {@link #update}. */
    @Override
    public void close() {
        progress.close();
    }

    /**
     * Retrieves the <i>minimum</i> value of progress that is possible.
     *
     * @return the minimum possible value.
     */
    public int getMin() {
        return progress.getMin();
    }

    /**
     * Retrieves the <i>maximum</i> value of progress that is possible.
     *
     * @return the maximum possible value.
     */
    public int getMax() {
        return progress.getMax();
    }

    /**
     * Assigns the <i>minimum</i> value of progress that is possible.
     *
     * @param min the minimum possible value
     */
    public void setMin(int min) {
        progress.setMin(min);
    }

    /**
     * Assigns the <i>maximum</i> value of progress that is possible.
     *
     * @param max the maximum possible value
     */
    public void setMax(int max) {
        progress.setMax(max);
    }
}
