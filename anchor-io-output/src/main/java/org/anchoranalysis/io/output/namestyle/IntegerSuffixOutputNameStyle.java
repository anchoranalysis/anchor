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

import com.google.common.base.Preconditions;

/**
 * Outputs a file-name involving an integer suffix of length {@code numberDigits} with leading
 * zeros.
 *
 * <p>Optionally, a prefix will be placed before this number (with an underscore to separate).
 *
 * <p>If no prefix is defined, no underscore will be present.
 *
 * @author Owen Feehan
 */
public class IntegerSuffixOutputNameStyle extends IndexableOutputNameStyle {

    /** */
    private static final long serialVersionUID = 0;

    /** A string placed before the numeric part of the naming-style. */
    private String prefix;

    /** A fixed number of digits (prefixed with leading zeros) for the numeric part. */
    private int numberDigits;

    /** Empty constructor, as needed for deserialization. */
    public IntegerSuffixOutputNameStyle() {
        // Here as the empty constructor is needed for deserialization
    }

    /**
     * Creates with an output-name and a number of digits.
     *
     * @param outputName the output-name.
     * @param numberDigits a fixed number of digits (prefixed with leading zeros) for the numeric
     *     part.
     */
    public IntegerSuffixOutputNameStyle(String outputName, int numberDigits) {
        super(outputName);
        Preconditions.checkArgument(numberDigits > 1);
        this.numberDigits = numberDigits;
    }

    /**
     * Creates with an output-name, a prefix, and a number of digits.
     *
     * @param outputName the output-name.
     * @param prefix a string placed before the numeric part of the naming-style.
     * @param numberDigits a fixed number of digits (prefixed with leading zeros) for the numeric
     *     part.
     */
    public IntegerSuffixOutputNameStyle(String outputName, String prefix, int numberDigits) {
        this(outputName, numberDigits);
        this.prefix = prefix;
    }

    private IntegerSuffixOutputNameStyle(IntegerSuffixOutputNameStyle src) {
        super(src);
        this.prefix = src.prefix;
        this.numberDigits = src.numberDigits;
    }

    @Override
    public IndexableOutputNameStyle duplicate() {
        return new IntegerSuffixOutputNameStyle(this);
    }

    @Override
    protected String filenameFromOutputFormatString(String outputFormatString, String index) {
        int indexInt = Integer.parseInt(index);
        return String.format(outputFormatString, indexInt);
    }

    @Override
    protected String outputFormatString() {
        return combineIntegerAndOutputName(integerFormatSpecifier(numberDigits));
    }

    private String combineIntegerAndOutputName(String integerFormatString) {
        if (!prefix.isEmpty()) {
            return prefix + "_" + integerFormatString;
        } else {
            return integerFormatString;
        }
    }

    private static String integerFormatSpecifier(int numDigits) {
        return "%0" + Integer.toString(numDigits) + "d";
    }
}
