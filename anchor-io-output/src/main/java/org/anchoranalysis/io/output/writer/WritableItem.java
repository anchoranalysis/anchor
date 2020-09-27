/*-
 * #%L
 * anchor-io-output
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

package org.anchoranalysis.io.output.writer;

import org.anchoranalysis.io.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.namestyle.OutputNameStyle;
import org.anchoranalysis.io.output.bound.OutputterChecked;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * An item that can be outputted via a write() method
 *
 * @author Owen Feehan
 */
public interface WritableItem {

    /**
     * Writes a non-indexable output (an output that isn't part of a collection of other similar
     * items)
     *
     * @param outputNameStyle
     * @param outputter
     * @throws OutputWriteFailedException
     */
    public abstract void write(OutputNameStyle outputNameStyle, OutputterChecked outputter)
            throws OutputWriteFailedException;

    /**
     * Writes an indexable output (many outputs of the same type, uniquely identified by an index)
     *
     * @param outputNameStyle
     * @param index
     * @param outputter
     * @return
     * @throws OutputWriteFailedException
     */
    public abstract int write(
            IndexableOutputNameStyle outputNameStyle,
            String index,
            OutputterChecked outputter)
            throws OutputWriteFailedException;
}
