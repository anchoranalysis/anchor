/* (C)2020 */
package org.anchoranalysis.io.output.file;

import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bound.BoundOutputManager;

public class FileOutputFromManager {

    private FileOutputFromManager() {}

    /**
     * Creates a FileOutput
     *
     * @param extension file extension
     * @param manifestDescription manifest description
     * @param outputManager output-manager
     * @param outputName output-name
     * @return the FileOutput or empty() if it the output is not allowed
     */
    public static Optional<FileOutput> create(
            String extension,
            Optional<ManifestDescription> manifestDescription,
            BoundOutputManager outputManager,
            String outputName) {

        Optional<Path> fileOutputPath =
                outputManager
                        .getWriterCheckIfAllowed()
                        .writeGenerateFilename(
                                outputName, extension, manifestDescription, "", "", "");
        return fileOutputPath.map(path -> new FileOutput(path.toString()));
    }
}
