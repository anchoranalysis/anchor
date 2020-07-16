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
