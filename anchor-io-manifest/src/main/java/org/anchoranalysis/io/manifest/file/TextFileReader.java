package org.anchoranalysis.io.manifest.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class TextFileReader {

    public static String readFile(Path path) throws IOException {

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
    
    private static BufferedReader createReader(Path path) throws FileNotFoundException {
        return new BufferedReader(new FileReader(path.toFile()));
    }
}
