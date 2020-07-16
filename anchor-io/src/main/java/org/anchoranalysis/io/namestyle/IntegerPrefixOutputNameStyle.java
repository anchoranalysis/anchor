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

package org.anchoranalysis.io.namestyle;

public class IntegerPrefixOutputNameStyle extends IntegerOutputNameStyle {

    /** */
    private static final long serialVersionUID = -3128734431534880903L;

    public IntegerPrefixOutputNameStyle() {
        // Here as the empty constructor is needed for deserialization
    }

    private IntegerPrefixOutputNameStyle(IntegerPrefixOutputNameStyle src) {
        super(src);
    }

    public IntegerPrefixOutputNameStyle(String outputName, int numDigitsInteger) {
        super(outputName, numDigitsInteger);
    }

    @Override
    public IndexableOutputNameStyle deriveIndexableStyle(int numDigits) {
        return new IntegerPrefixOutputNameStyle(this.getOutputName(), numDigits);
    }

    @Override
    public IndexableOutputNameStyle duplicate() {
        return new IntegerPrefixOutputNameStyle(this);
    }

    @Override
    protected String combineIntegerAndOutputName(String outputName, String integerFormatString) {
        return integerFormatString + "_" + outputName;
    }
}
