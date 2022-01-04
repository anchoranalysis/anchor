/*-
 * #%L
 * anchor-io-generator
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
package org.anchoranalysis.io.generator.sequence.pattern;

import java.util.Optional;
import org.anchoranalysis.io.output.namestyle.IntegerSuffixOutputNameStyle;

/**
 * A {@link OutputPattern} that outputs a file-name with a trailing integer index.
 *
 * @author Owen Feehan
 */
public class OutputPatternIntegerSuffix extends OutputPattern {

    private static final int DEFAULT_NUMBER_DIGITS = 6;

    /**
     * Creates in a subdirectory, using the outputName as both the subdirectory name and the prefix.
     *
     * <p>The {@code subdirectoryName} is also used as a prefix on outputted files.
     *
     * <p>The full-name has an increment number appended e.g. <code>
     * $outputName/$outputName_000000.tif</code> etc.
     *
     * @param outputName name of subdirectory to place sequence in (which is also used as the
     *     outputName and prefix)
     * @param selective whether to check output-names against the rules or not. If not, all outputs
     *     will occur permissively.
     */
    public OutputPatternIntegerSuffix(String outputName, boolean selective) {
        this(outputName, DEFAULT_NUMBER_DIGITS, selective);
    }

    /**
     * Creates in a subdirectory, using the output-name as the subdirectory name, and a specific
     * prefix.
     *
     * <p>The full-name has an increment number appended e.g. <code>$outputName/$prefix_000000.tif
     * </code> etc.
     *
     * @param outputName name of subdirectory to place sequence in (which is also used as the
     *     outputName and prefix)
     * @param prefix a string that appears before the index in each outputted filename, with an
     *     underscore separating the prefix from the numeric index
     */
    public OutputPatternIntegerSuffix(String outputName, String prefix) {
        this(outputName, false, prefix, DEFAULT_NUMBER_DIGITS, true);
    }

    /**
     * Like {@link #OutputPatternIntegerSuffix(String, boolean)} but also whether to check outputs
     * against rules.
     *
     * @param outputName name of subdirectory to place sequence in (which is also used as the
     *     outputName and prefix)
     * @param numberDigits the number of digits in the numeric part of the output-name.
     * @param selective whether to check output-names against the rules or not. If not, all outputs
     *     will occur permissively.
     */
    public OutputPatternIntegerSuffix(String outputName, int numberDigits, boolean selective) {
        this(outputName, false, outputName, numberDigits, selective);
    }

    /**
     * Like {@link #OutputPatternIntegerSuffix(String, String)} but with additional options about
     * the number of digits, and whether to check outputs against rules.
     *
     * @param outputName name of subdirectory to place sequence in (which is also used as the
     *     outputName and prefix)
     * @param prefix a string that appears before the index in each outputted filename, with an
     *     underscore separating the prefix from the numeric index
     * @param numberDigits the number of digits in the numeric part of the output-name.
     * @param selective whether to check output-names against the rules or not. If not, all outputs
     *     will occur permissively.
     */
    public OutputPatternIntegerSuffix(
            String outputName, String prefix, int numberDigits, boolean selective) {
        super(
                Optional.of(outputName),
                new IntegerSuffixOutputNameStyle(outputName, prefix, numberDigits),
                selective);
    }

    /**
     * Creates with a full set of flexibility about how the filename appears and whether a
     * subdirectory is used.
     *
     * @param outputName the output-name to use, which also determines the subdirectory name if it
     *     is not suppressed.
     * @param suppressSubdirectory if true, a separate subdirectory is not created, and rather the
     *     outputs occur in the parent directory.
     * @param prefix a string that appears before the index in each outputted filename, with an
     *     underscore separating the prefix from the numeric index
     * @param numberDigits the number of digits in the numeric part of the output-name.
     * @param selective whether to check output-names against the rules or not. If not, all outputs
     *     will occur permissively.
     */
    public OutputPatternIntegerSuffix(
            String outputName,
            boolean suppressSubdirectory,
            String prefix,
            int numberDigits,
            boolean selective) {
        super(
                OutputPatternUtilities.maybeSubdirectory(outputName, suppressSubdirectory),
                new IntegerSuffixOutputNameStyle(outputName, prefix, numberDigits),
                selective);
    }
}
