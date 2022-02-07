/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.histogram.input;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.io.input.csv.CSVReaderByLine;
import org.anchoranalysis.io.input.csv.CSVReaderException;
import org.anchoranalysis.io.input.csv.ReadByLine;
import org.anchoranalysis.math.histogram.Histogram;

/**
 * Reads a CSV file from the file-system that describes a histogram of voxel values.
 *
 * @author owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistogramCSVReader {

    /**
     * Reads a CSV file from the file-system that describes a histogram of voxel values.
     *
     * @param filePath the path to the CSV file.
     * @return a newly created {@link Histogram} representing the values in the file.
     * @throws CSVReaderException if any file I/O errors, or otherwise the histogram cannot be
     *     successfully created.
     */
    public static Histogram readHistogramFromFile(Path filePath) throws CSVReaderException {

        Map<Integer, Integer> map = new HashMap<>();

        try (ReadByLine reader = CSVReaderByLine.open(filePath)) {
            reader.read((line, firstLine) -> addLineToMap(map, line));
        }

        return histogramFromMap(map);
    }

    /**
     * Processes a line of elements, so that entites are added to {@code map}.
     *
     * @param map a mapping of bin identifiers to counts for the histogram.
     * @param line the line being processed.
     */
    private static void addLineToMap(Map<Integer, Integer> map, String[] line)
            throws OperationFailedException {

        float binAsFloat = Float.parseFloat(line[0]);
        int bin = (int) binAsFloat;

        if (binAsFloat != bin) {
            throw new OperationFailedException(
                    String.format("Bin-value of %f is not integer.", binAsFloat));
        }

        float countAsFloat = Float.parseFloat(line[1]);
        int count = (int) countAsFloat;

        if (countAsFloat != count) {
            throw new OperationFailedException(
                    String.format("Count-value of %f is not integer.", countAsFloat));
        }

        if (map.containsKey(bin)) {
            throw new OperationFailedException(
                    String.format("There are multiple bins of value %d", bin));
        }

        map.put(bin, count);
    }

    /** Maximum value in {@code set}. */
    private static int maxValue(Set<Integer> set) {
        return set.stream().mapToInt(Integer::intValue).max().getAsInt(); // NOSONAR
    }

    /** Creates a {@link Histogram} from a mapping of bins to counts. */
    private static Histogram histogramFromMap(Map<Integer, Integer> map) throws CSVReaderException {

        // We get the highest-intensity value from the map
        int maxCsvValue = maxValue(map.keySet());

        // We guess the upper limit of the histogram to match an unsigned 8-bit or 16-bit image
        int maxHistogramValue = guessMaxHistogramBin(maxCsvValue);

        Histogram histogram = new Histogram(maxHistogramValue);

        for (Entry<Integer, Integer> entry : map.entrySet()) {
            histogram.incrementValueBy(entry.getKey(), entry.getValue());
        }
        return histogram;
    }

    /**
     * Guess the maximum possible histogram bin given maximum bin in the CSV file.
     *
     * <p>This guesses which voxel data-type was originally used to make the histogram.
     */
    private static int guessMaxHistogramBin(int maxCsvBin) throws CSVReaderException {
        if (maxCsvBin <= UnsignedByteVoxelType.MAX_VALUE) {
            return UnsignedByteVoxelType.MAX_VALUE_INT;
        } else if (maxCsvBin <= UnsignedShortVoxelType.MAX_VALUE) {
            return UnsignedShortVoxelType.MAX_VALUE_INT;
        } else {
            throw new CSVReaderException(
                    "Histograms can only supported for a maximum-value of "
                            + UnsignedShortVoxelType.MAX_VALUE_INT);
        }
    }
}
