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

import java.io.PrintStream;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import org.anchoranalysis.io.csv.reader.CSVReader;
import org.anchoranalysis.io.csv.reader.CSVReader.OpenedCSVFile;
import org.anchoranalysis.io.csv.reader.CSVReaderException;
import org.apache.commons.lang.ArrayUtils;

@AllArgsConstructor
public class CSVComparer {

    /** The separator for the csv file (a regular expression for split function) */
    private String regExSeperator;

    /** Whether the first line of the CSV file headers */
    private boolean firstLineHeaders;

    /** Ignore the first number of columns (left most) when measuring equality */
    private int ignoreFirstNumColumns;

    /**
     * If true, all lines in the CSV file are sorted before comparison. If false, the order remains
     * unchanged.
     */
    private boolean sortLines;

    /** An exception is thrown if the CSV file has zero rows */
    private boolean rejectZeroRows;

    /**
     * Are two CSV files equal?
     *
     * @param path1 path to first file to compare, without any lines having been already read
     * @param path2 path to second file to compare, without any lines having been already read
     * @param messageStream if CSV files aren't equal, additional explanation messages are printed
     *     here
     * @throws CSVReaderException if something goes wrong
     */
    public boolean areCsvFilesEqual(Path path1, Path path2, PrintStream messageStream)
            throws CSVReaderException {

        OpenedCSVFile file1 = openCsvFromFilePath(path1);
        OpenedCSVFile file2 = openCsvFromFilePath(path2);

        if (firstLineHeaders && !checkHeadersIdentical(file1, file2, messageStream)) {
            return false;
        }

        if (sortLines) {
            CompareSorted compareWithSorting =
                    new CompareSorted(ignoreFirstNumColumns, rejectZeroRows);
            return compareWithSorting.compare(file1, file2, System.out); // NOSONAR
        } else {
            CompareUnsorted compareUnsorted = new CompareUnsorted();
            return compareUnsorted.compareCsvFilesWithoutSorting(
                    file1, file2, ignoreFirstNumColumns, rejectZeroRows);
        }
    }

    private static boolean checkHeadersIdentical(
            OpenedCSVFile file1, OpenedCSVFile file2, PrintStream messageStream) {
        String[] headers1 = file1.getHeaders();
        String[] headers2 = file2.getHeaders();
        if (ArrayUtils.isEquals(headers1, headers2)) {
            return true;
        } else {
            messageStream.println("Headers are not identical:");
            CompareUtilities.printTwoLines(messageStream, headers1, headers2);
            return false;
        }
    }

    /**
     * Opens a CSV file from an absolute filePath
     *
     * @param filePath absolute file-path
     * @return a CSVFile object, but without any rows having been read
     * @throws CSVReaderException
     */
    private OpenedCSVFile openCsvFromFilePath(Path filePath) throws CSVReaderException {
        CSVReader csvReader = new CSVReader(regExSeperator, firstLineHeaders);
        return csvReader.read(filePath);
    }
}
