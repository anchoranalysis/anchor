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

import java.util.Optional;

/**
 * Outputs a file-name involving a string suffix.
 *
 * @author Owen Feehan
 */
public class StringSuffixOutputNameStyle extends IndexableOutputNameStyle {

    /** */
    private static final long serialVersionUID = 4582344790084798682L;

    private String outputFormatString;

    /** Empty constructor, as needed for deserialization. */
    public StringSuffixOutputNameStyle() {
        // Here as the empty constructor is needed for deserialization
    }

    /**
     * Creates with an output-name.
     *
     * @param outputName the output-name (without any prefix).
     * @param prefix an optional prefix that is prepended to {@code outputName}.
     */
    public StringSuffixOutputNameStyle(String outputName, Optional<String> prefix) {
        super(outputName);
        if (prefix.isPresent()) {
            this.outputFormatString = prefix.get() + "%s";
        } else {
            this.outputFormatString = "%s";
        }
    }

    @Override
    public IndexableOutputNameStyle duplicate() {
        return new StringSuffixOutputNameStyle(this);
    }

    @Override
    protected String filenameFromOutputFormatString(String outputFormatString, String index) {
        return String.format(outputFormatString, index);
    }

    @Override
    protected String outputFormatString() {
        return outputFormatString;
    }

    private StringSuffixOutputNameStyle(StringSuffixOutputNameStyle source) {
        super(source);
    }
}
