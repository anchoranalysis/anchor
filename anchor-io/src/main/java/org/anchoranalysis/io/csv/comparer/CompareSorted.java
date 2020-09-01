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

package org.anchoranalysis.io.csv.comparer;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.TreeSet;
import org.anchoranalysis.io.csv.reader.CSVReader.OpenedCSVFile;
import org.anchoranalysis.io.csv.reader.CSVReaderException;

/**
 * Compares csv files after sorting their rows in a deterministic order
 *
 * @author Owen Feehan
 */
class CompareSorted {

    private TreeSet<String[]> sortedLines1;
    private TreeSet<String[]> sortedLines2;
    private int ignoreFirstNumColumns;
    private boolean rejectZeroRows;

    /**
     * Constructor
     *
     * @param ignoreFirstNumColumns ignore the first number of columns in the comparison
     * @param rejectZeroRows throws an exception if the CSV file has zero rows
     */
    public CompareSorted(int ignoreFirstNumColumns, boolean rejectZeroRows) {
        this.ignoreFirstNumColumns = ignoreFirstNumColumns;
        this.rejectZeroRows = rejectZeroRows;

        // Only static access
        Comparator<String[]> comparator = new StringArrayComparator();
        sortedLines1 = new TreeSet<>(comparator);
        sortedLines2 = new TreeSet<>(comparator);
    }

    /**
     * Compares two csv files after first sorting their lines, and ignoring a certain number of
     * initial columns
     *
     * @param file1 first file to compare
     * @param file2 second file to compare
     * @param messageStream if non-equal, additional explanation messages are printed here
     * @return true if they are identical (subject to the conditions above), false otherwise
     * @throws CSVReaderException if something goes wrong accessing the csv files
     */
    public boolean compare(OpenedCSVFile file1, OpenedCSVFile file2, PrintStream messageStream)
            throws CSVReaderException {

        sortedLines1.clear();
        sortedLines2.clear();

        if (!loadFromFiles(file1, file2)) {
            messageStream.println("files have a different number of lines");
            // Clearly they are not identical, if they have different numbers of lines
            return false;
        }

        return compareSets(messageStream);
    }

    /**
     * Loads the tree-sets from the two files
     *
     * @param file1
     * @param file2
     * @return true if both files have equal numbers of lines, false otherwise
     */
    private boolean loadFromFiles(OpenedCSVFile file1, OpenedCSVFile file2)
            throws CSVReaderException {
        try {
            boolean first = true;

            while (true) {
                Optional<String[]> lines1 = file1.readLine();
                Optional<String[]> lines2 = file2.readLine();

                if (first) {
                    CompareUtilities.checkZeroRows(rejectZeroRows, lines1, lines2);
                    first = false;
                }

                if (!lines1.isPresent() || !lines2.isPresent()) {
                    return !lines1.isPresent() && !lines2.isPresent();
                }

                sortedLines1.add(lines1.get());
                sortedLines2.add(lines2.get());
            }
        } catch (IOException e) {
            throw new CSVReaderException(e);
        }
    }

    /**
     * Compares each item in order drawn from the sets
     *
     * @param messageStream if non-equal, additional explanation messages are printed here
     * @return true if all items are exactly identical, false otherwise
     */
    private boolean compareSets(PrintStream messageStream) {
        Iterator<String[]> itr1 = sortedLines1.iterator();
        Iterator<String[]> itr2 = sortedLines2.iterator();
        while (itr1.hasNext()) {
            assert itr2.hasNext();

            String[] line1 = itr1.next();
            String[] line2 = itr2.next();

            if (!CompareUtilities.areArraysEqual(
                    Optional.of(line1), Optional.of(line2), ignoreFirstNumColumns)) {

                messageStream.println("The following lines are not identical:");
                CompareUtilities.printTwoLines(messageStream, line1, line2, "  ");

                return false;
            }
        }

        return true;
    }

    /**
     * Imposes an ordering on arrays of strings by 1. first doing a compareTo on the length of the
     * string arrays 2. then if the result is 0, doing a compareTo on each string incrementally
     * until a non-zero result is found
     *
     * @author Owen Feehan
     */
    private static class StringArrayComparator implements Comparator<String[]> {

        @Override
        public int compare(String[] arg0, String[] arg1) {

            // First we compare string lengths
            int compare = Integer.compare(arg0.length, arg1.length);

            // If they are different we exit early
            if (compare != 0) {
                return compare;
            }

            // Now we incrementally compare all the strings until we have a nonzero result
            for (int i = 0; i < arg0.length; i++) {
                String str0 = arg0[i];
                String str1 = arg1[i];

                compare = str0.compareTo(str1);
                if (compare != 0) {
                    return compare;
                }
            }

            // This must be zero if we get this far, implying the string arrays are identical
            return 0;
        }
    }
}
