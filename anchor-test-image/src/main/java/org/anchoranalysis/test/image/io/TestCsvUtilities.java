/* (C)2020 */
package org.anchoranalysis.test.image.io;

import java.io.IOException;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.csv.reader.CSVReader.OpenedCSVFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestCsvUtilities {

    /**
     * Checks if a particular string can be found in any cell of a CSV file
     *
     * @param file a csv-file to search through its cells
     * @param str string to search for in any of the cells in the CsvFile (it may also be a
     *     substring of the cell)
     * @return TRUE if at least one instance of str is found, FALSE otherwise
     * @throws IOException if something goes wrong reading the csv-file
     */
    public static boolean doesCsvFileContainString(OpenedCSVFile file, String str)
            throws IOException {

        Optional<String[]> line;
        while ((line = file.readLine()).isPresent()) {
            for (int i = 0; i < line.get().length; i++) {

                if (line.get()[i].contains(str)) {
                    return true;
                }
            }
        }
        return false;
    }
}
