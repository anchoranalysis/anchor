/* (C)2020 */
package org.anchoranalysis.io.csv.comparer;

import static org.anchoranalysis.io.csv.comparer.CompareUtilities.*;

import java.io.IOException;
import java.util.Optional;
import org.anchoranalysis.io.csv.reader.CSVReader.OpenedCSVFile;
import org.anchoranalysis.io.csv.reader.CSVReaderException;

class CompareUnsorted {

    public boolean compareCsvFilesWithoutSorting(
            OpenedCSVFile file1,
            OpenedCSVFile file2,
            int ignoreFirstNumColumns,
            boolean rejectZeroRows)
            throws CSVReaderException {
        try {
            boolean first = true;

            while (true) {
                Optional<String[]> lines1 = file1.readLine();
                Optional<String[]> lines2 = file2.readLine();

                if (first) {
                    checkZeroRows(rejectZeroRows, lines1, lines2);
                    first = false;
                }

                if (!areArraysEqual(lines1, lines2, ignoreFirstNumColumns)) {
                    return false;
                }

                if (!lines1.isPresent()) {
                    // lines2 must also be null by this point
                    return true;
                }
            }
        } catch (IOException e) {
            throw new CSVReaderException(e);
        }
    }
}
