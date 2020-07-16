/* (C)2020 */
package org.anchoranalysis.io.generator.text;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;

public class WriteStringToFile {

    private WriteStringToFile() {}

    public static void apply(String element, Path filePath) throws IOException {
        FileWriter outFile = new FileWriter(filePath.toFile());
        PrintWriter out = new PrintWriter(outFile);

        out.println(element);

        out.close();
    }
}
