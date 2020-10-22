/*-
 * #%L
 * anchor-io
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

package org.anchoranalysis.io.output.namestyle;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * An output-name with an index somehow appended or prepended in a particular style.
 *
 * <p>The no-arguments constructor exists only for deserialization.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
@AllArgsConstructor
public abstract class IndexableOutputNameStyle extends OutputNameStyle {

    private static final long serialVersionUID = -2393013576294162543L;

    /** The output-name (without any index). */
    private String outputName;

    /**
     * Copy constructor
     *
     * @param source source
     */
    protected IndexableOutputNameStyle(IndexableOutputNameStyle source) {
        this.outputName = source.outputName;
    }

    /**
     * Like {@link #getFilenameWithoutExtension()} but incorporates an <i>integer</i> index.
     *
     * @param index the index
     * @return @return the filename (without an extension, including without the period before the
     *     extension).
     */
    public String getFilenameWithoutExtension(int index) {
        return getFilenameWithoutExtension(Integer.toString(index));
    }

    /**
     * Like {@link #getFilenameWithoutExtension()} but incorporates an <i>string</i> index.
     *
     * @param index the index
     * @return @return the filename (without an extension, including without the period before the
     *     extension).
     */
    public String getFilenameWithoutExtension(String index) {
        return nameFromOutputFormatString(outputFormatString(), index);
    }

    @Override
    public abstract IndexableOutputNameStyle duplicate();

    @Override
    public String getOutputName() {
        return outputName;
    }

    @Override
    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    @Override
    public String getFilenameWithoutExtension() {
        throw new UnsupportedOperationException(
                "an index is required for getPhysicalName in this class");
    }

    /** Constructs a full name from the output format string and an index */
    protected abstract String nameFromOutputFormatString(String outputFormatString, String index);

    protected abstract String outputFormatString();
}
