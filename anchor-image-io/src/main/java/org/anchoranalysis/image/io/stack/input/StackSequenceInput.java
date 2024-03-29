/*-
 * #%L
 * anchor-plugin-io
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

package org.anchoranalysis.image.io.stack.input;

import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.stack.Stack; // NOSONAR
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.stack.time.TimeSeries;

/**
 * Provides a single stack (or a time series of such stacks) as an input
 *
 * @author Owen Feehan
 */
public interface StackSequenceInput extends ProvidesStackInput {

    /**
     * Creates a supplier of a {@link TimeSeries} of stacks for a particular series number.
     *
     * @param seriesIndex the index.
     * @param logger a logger for any non-fatal errors. Fatal errors throw an exception.
     * @return a newly created {@link TimeSeries} of {@link Stack}s.
     * @throws ImageIOException if any stack could not be successfully read.
     */
    TimeSeries createStackSequenceForSeries(int seriesIndex, Logger logger) throws ImageIOException;
}
