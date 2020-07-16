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
/* (C)2020 */
package org.anchoranalysis.io.namestyle;

public class StringSuffixOutputNameStyle extends IndexableOutputNameStyle {

    /** */
    private static final long serialVersionUID = 4582344790084798682L;

    private String outputFormatString;

    public StringSuffixOutputNameStyle() {
        // Here as the empty constructor is needed for deserialization
    }

    public StringSuffixOutputNameStyle(String outputName, String outputFormatString) {
        super(outputName);
        this.outputFormatString = outputFormatString;
    }

    private StringSuffixOutputNameStyle(StringSuffixOutputNameStyle src) {
        super(src);
    }

    @Override
    protected String nameFromOutputFormatString(String outputFormatString, String index) {
        return String.format(outputFormatString, index);
    }

    @Override
    public IndexableOutputNameStyle deriveIndexableStyle(int numDigits) {
        // Number-of-digits is ignored and not relevant
        return duplicate();
    }

    @Override
    public IndexableOutputNameStyle duplicate() {
        return new StringSuffixOutputNameStyle(this);
    }

    @Override
    protected String outputFormatString() {
        return outputFormatString;
    }
}
