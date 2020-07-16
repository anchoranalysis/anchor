/*-
 * #%L
 * anchor-image-experiment
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
/* (C)2020 */
package org.anchoranalysis.image.experiment.label;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderByLine.ReadByLine;
import org.anchoranalysis.io.csv.reader.CSVReaderException;

/**
 * @author feehano
 * @param <T> key-type
 */
public class FileLabelMap<T> {

    private Map<T, String> map = new HashMap<>();

    public void add(T fileId, String label) {
        map.put(fileId, label);
    }

    public String get(T fileId) {
        return map.get(fileId);
    }

    public Set<String> labels() {
        return new HashSet<>(map.values());
    }

    public static FileLabelMap<String> readFromCSV(Path csvPath, boolean quotedStrings)
            throws CSVReaderException {
        FileLabelMap<String> map = new FileLabelMap<>();

        try (ReadByLine reader = CSVReaderByLine.open(csvPath, ",", true, quotedStrings)) {
            reader.read((line, firstLine) -> map.add(line[0], line[1]));
        }

        return map;
    }

    public Set<Entry<T, String>> entrySet() {
        return map.entrySet();
    }
}
