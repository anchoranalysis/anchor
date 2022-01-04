package org.anchoranalysis.image.inference.bean.segment.instance;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TextFileReader {

    /**
     * Reads all the lines in a text-file into a string.
     *
     * @param path to the text file to read.
     * @return the string
     * @throws IOException if a text-file cannot be read.
     */
    public static String readFileAsString(Path path) throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = createReader(path)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
        }

        return stringBuilder.toString();
    }

    /**
     * Reads all the lines in a text-file into a list of strings.
     *
     * @param path to the text file to read.
     * @return the strings
     * @throws IOException if a text-file cannot be read.
     */
    public static List<String> readLinesAsList(Path path) throws IOException {

        List<String> list = new ArrayList<>();

        String line;
        try (BufferedReader reader = createReader(path)) {
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        }

        return list;
    }

    private static BufferedReader createReader(Path path) throws FileNotFoundException {
        return new BufferedReader(new FileReader(path.toFile()));
    }
}
