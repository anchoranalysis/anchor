/* (C)2020 */
package org.anchoranalysis.image.io.histogram;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramArray;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine.ReadByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderException;

public class HistogramCSVReader {

    private HistogramCSVReader() {}

    public static Histogram readHistogramFromFile(Path filePath) throws CSVReaderException {

        Map<Integer, Integer> map = new HashMap<>();

        try (ReadByLine reader = CSVReaderByLine.open(filePath)) {
            reader.read((line, firstLine) -> addLineToMap(map, line));
        }

        return histogramFromMap(map);
    }

    private static void addLineToMap(Map<Integer, Integer> map, String[] line)
            throws OperationFailedException {

        float binF = Float.parseFloat(line[0]);
        int bin = (int) binF;

        if (binF != bin) {
            throw new OperationFailedException(
                    String.format("Bin-value of %f is not integer.", binF));
        }

        float countF = Float.parseFloat(line[1]);
        int count = (int) countF;

        if (countF != count) {
            throw new OperationFailedException(
                    String.format("Count-value of %f is not integer.", countF));
        }

        if (map.containsKey(bin)) {
            throw new OperationFailedException(
                    String.format("There are multiple bins of value %d", bin));
        }

        map.put(bin, count);
    }

    // Maximum-value
    private static int maxVal(Set<Integer> set) {

        Integer max = null;
        for (Integer i : set) {
            if (max == null || i > max) {
                max = i;
            }
        }
        return max;
    }

    private static int guessMaxHistVal(int maxBinVal) {
        if (maxBinVal <= 255) {
            return 255;
        } else {
            return 65535;
        }
    }

    private static Histogram histogramFromMap(Map<Integer, Integer> map) {

        // We get the highest-intensity value from the map
        int maxCSVVal = maxVal(map.keySet());

        // We guess the upper limit of the histogram to match an unsigned 8-bit or 16-bit image
        int maxHistVal = guessMaxHistVal(maxCSVVal);

        Histogram hist = new HistogramArray(maxHistVal);

        for (Entry<Integer, Integer> entry : map.entrySet()) {
            hist.incrValBy(entry.getValue(), entry.getKey());
        }
        return hist;
    }
}
