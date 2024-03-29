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

import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import org.anchoranalysis.io.output.namestyle.IndexableOutputNameStyle;
import org.anchoranalysis.io.output.namestyle.OutputNameStyle;

/**
 * Writes an element (with or without an index) to the file system.
 *
 * @author Owen Feehan
 * @param <T> the type of element to be written
 */
public interface ElementWriter<T> {

    /**
     * Writes a non-indexable output (an output that isn't part of a collection of other similar
     * items).
     *
     * @param element the element to write.
     * @param outputNameStyle how to write output-names.
     * @param outputter how the element is outputted.
     * @throws OutputWriteFailedException if the write operation fails.
     */
    void write(T element, OutputNameStyle outputNameStyle, ElementOutputter outputter)
            throws OutputWriteFailedException;

    /**
     * Writes an indexable output (many outputs of the same type, uniquely identified by an index).
     *
     * @param element the element to write.
     * @param index a string that uniquely identifies this element compared to others in the same
     *     write operation.
     * @param outputNameStyle how to write output-names.
     * @param outputter how the element is outputted.
     * @throws OutputWriteFailedException if the write operation fails.
     */
    void writeWithIndex(
            T element,
            String index,
            IndexableOutputNameStyle outputNameStyle,
            ElementOutputter outputter)
            throws OutputWriteFailedException;
}
