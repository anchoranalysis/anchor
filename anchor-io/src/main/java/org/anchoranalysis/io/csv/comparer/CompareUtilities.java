/* (C)2020 */
package org.anchoranalysis.io.csv.comparer;

import java.io.PrintStream;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.csv.reader.CSVReaderException;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CompareUtilities {

    public static void checkZeroRows(
            boolean rejectZeroRows, Optional<String[]> lines1, Optional<String[]> lines2)
            throws CSVReaderException {
        if (!rejectZeroRows) {
            return;
        }

        if (!lines1.isPresent() || !lines2.isPresent()) {
            throw new CSVReaderException("At least one input csv file has zero rows");
        }
    }

    /**
     * Are arrays equals?
     *
     * @param lines1
     * @param lines2
     * @param ignoreFirstNumColumns
     * @return
     */
    public static boolean areArraysEqual(
            Optional<String[]> lines1, Optional<String[]> lines2, int ignoreFirstNumColumns) {

        if (!lines1.isPresent()) {
            return !lines2.isPresent();
        }
        if (!lines2.isPresent()) {
            return !lines1.isPresent();
        }

        if (lines1.get().length != lines2.get().length) {
            return false;
        }

        if (ignoreFirstNumColumns > 0) {

            int maxInd = Math.max(lines1.get().length - ignoreFirstNumColumns, 0);

            for (int i = ignoreFirstNumColumns; i < maxInd; i++) {
                if (!lines1.get()[i].equals(lines2.get()[i])) {
                    return false;
                }
            }
            return true;

        } else {
            // The simple case where we don't ignore any columns
            return ArrayUtils.isEquals(lines1.get(), lines2.get());
        }
    }

    /**
     * Prints two lines (represented by string arrays) to the screen, ensuring that each array item
     * is presented in vertical columns
     *
     * <p>Default delimeter of " "
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
     * @param delimter a string that separates each column
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
                // Write out the delimter
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
