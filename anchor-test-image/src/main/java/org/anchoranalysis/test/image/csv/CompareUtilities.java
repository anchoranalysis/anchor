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

package org.anchoranalysis.test.image.csv;

import java.io.PrintStream;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.input.csv.CSVReaderException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Utility class for comparing and printing CSV-related data.
 * 
 * <p>This class provides methods for checking CSV rows, comparing string arrays,
 * and printing formatted output for CSV comparisons.</p>
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CompareUtilities {

    /**
     * Checks if either of the input arrays is empty when zero rows are not allowed.
     *
     * @param rejectZeroRows if true, an exception is thrown when either input array is empty
     * @param lines1 the first array of strings to check
     * @param lines2 the second array of strings to check
     * @throws CSVReaderException if rejectZeroRows is true and either lines1 or lines2 is empty
     */
    public static void checkZeroRows(
            boolean rejectZeroRows, Optional<String[]> lines1, Optional<String[]> lines2)
            throws CSVReaderException {
        if (rejectZeroRows) {
        	if (!lines1.isPresent() || !lines2.isPresent()) {
                throw new CSVReaderException("At least one input csv file has zero rows");
            }
        }
    }

    /**
     * Are two arrays of strings equals?
     * 
     * <p>To be equal, the same number of elements must exist in both arrays, and each element must be identical.
     *
     * <p>Exceptionally, an array is an {@link Optional} which can also be {@link Optional#empty} which is also considered in the comparison.
     * 
     * @param array1 the first array to be compared.
     * @param array2 the second array to be compared.
     * @param ignoreInitialElements when positive, this many of the initial array elements are omitted for consideration in the comparison. when zero, all elements are considered.
     * @return true if all the above conditions for equality are met.
     */
    public static boolean areArraysEqual(
            Optional<String[]> array1, Optional<String[]> array2, int ignoreInitialElements) {

        if (!array1.isPresent()) {
            return !array2.isPresent();
        }
        if (!array2.isPresent()) {
            return !array1.isPresent();
        }

        if (array1.get().length != array2.get().length) {
            return false;
        }

        if (ignoreInitialElements > 0) {

            int maxInd = Math.max(array1.get().length - ignoreInitialElements, 0);

            for (int i = ignoreInitialElements; i < maxInd; i++) {
                if (!array1.get()[i].equals(array2.get()[i])) {
                    return false;
                }
            }
            return true;

        } else {
            // The simple case where we don't ignore any columns
            return ArrayUtils.isEquals(array1.get(), array2.get());
        }
    }

    /**
     * Prints two lines (represented by string arrays) to the screen, ensuring that each array item
     * is presented in vertical columns
     *
     * <p>Default delimiter of "  " (two spaces)
     *
     * @param messageStream the PrintStream to which the formatted lines will be written
     * @param line1 an array of strings for the first line
     * @param line2 an array of strings for the second line
     */
    public static void printTwoLines(PrintStream messageStream, String[] line1, String[] line2) {
        printTwoLines(messageStream, line1, line2, "  ");
    }

    /**
     * Prints two lines (represented by string arrays) to the screen, ensuring that each array item
     * is presented in vertical columns
     *
     * @param messageStream if non-equal, additional explanation messages are printed here
     * @param line1 an array of strings for first line
     * @param line2 an array of strings for second line
     * @param delimeter a string that separates each column
     */
    public static void printTwoLines(
            PrintStream messageStream, String[] line1, String[] line2, String delimeter) {

        StringBuilder out1 = new StringBuilder();
        StringBuilder out2 = new StringBuilder();

        if (line1.length != line2.length) {
            throw new IllegalArgumentException(
                    "line1 and line2 have a different number of elements");
        }
        for (int i = 0; i < line1.length; i++) {

            if (i != 0) {
                // If not the first item
                // Write out the delimiter
                out1.append(delimeter);
                out2.append(delimeter);
            }

            String elem1 = line1[i];
            String elem2 = line2[i];

            // Write out both elements, padding whichever is shorter
            int maxLength = Integer.max(elem1.length(), elem2.length());
            out1.append(StringUtils.rightPad(elem1, maxLength));
            out2.append(StringUtils.rightPad(elem2, maxLength));
        }

        messageStream.println(out1.toString());
        messageStream.println(out2.toString());
    }
}