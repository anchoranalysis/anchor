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

/**
 * Outputs a file-name involving an integer of length {@code numberDigits} with leading zeros.
 * 
 * <p>Optionally, a prefix will be placed before this number (with an underscore to separate).
 * 
 * <p>If no prefix is defined, no underscore will be present.
 * 
 * @author Owen Feehan
 *
 */
public class IntegerSuffixOutputNameStyle extends IntegerOutputNameStyle {

    /** */
    private static final long serialVersionUID = -3128734431534880903L;

    // A string placed before the numberic part of the naming-style
    private String prefix;
    
    public IntegerSuffixOutputNameStyle() {
        // Here as the empty constructor is needed for deserialization
    }

    private IntegerSuffixOutputNameStyle(IntegerSuffixOutputNameStyle src) {
        super(src);
        this.prefix = src.prefix;
    }

    public IntegerSuffixOutputNameStyle(String outputName, int numberDigits) {
        this(outputName, outputName, numberDigits);
    }
    
    public IntegerSuffixOutputNameStyle(String outputName, String prefix, int numberDigits) {
        super(outputName, numberDigits);
        this.prefix = prefix;
    }

    @Override
    public IndexableOutputNameStyle deriveIndexableStyle(int numDigits) {
        return new IntegerSuffixOutputNameStyle(this.getOutputName(), numDigits);
    }

    @Override
    public IndexableOutputNameStyle duplicate() {
        return new IntegerSuffixOutputNameStyle(this);
    }

    @Override
    protected String combineIntegerAndOutputName(String outputName, String integerFormatString) {
        if (!prefix.isEmpty()) {
            return prefix + "_" + integerFormatString;    
        } else {
            return integerFormatString;
        }
    }
}
