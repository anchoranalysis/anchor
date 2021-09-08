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
 * Tracks the completion of an operation, as divided into sub-units of progress.
 *
 * <p>Unlike {@link ProgressIncrement}, a value is explictly communicated to represent the progress
 * state.
 *
 * @author Owen Feehan
 */
public interface Progress extends AutoCloseable {

    /** Begins tracking progress, to be called before any calls to {@link #update}. */
    void open();

    /**
     * Informs the tracker of an update in the progress.
     *
     * @param value how much progress has occurred.
     */
    void update(int value);

    /** Ends tracking progress, to be called after any calls to {@link #update}. */
    @Override
    void close();

    /**
     * Retrieves the <i>minimum</i> value of progress that is possible.
     *
     * @return the minimum possible value.
     */
    int getMin();

    /**
     * Retrieves the <i>maximum</i> value of progress that is possible.
     *
     * @return the maximum possible value.
     */
    int getMax();

    /**
     * Assigns the <i>minimum</i> value of progress that is possible.
     *
     * @param min the minimum possible value
     */
    void setMin(int min);

    /**
     * Assigns the <i>maximum</i> value of progress that is possible.
     *
     * @param max the maximum possible value
     */
    void setMax(int max);
}
