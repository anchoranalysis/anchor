/* (C)2020 */
package org.anchoranalysis.experiment.log.reporter;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.bound.BoundOutputManager;
import org.anchoranalysis.io.output.file.FileOutput;
import org.anchoranalysis.io.output.file.FileOutputFromManager;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class TextFileLogHelper {

    public static Optional<FileOutput> createOutput(BoundOutputManager bom, String outputName) {
        return FileOutputFromManager.create(
                "txt",
                Optional.of(new ManifestDescription("textlog", "messageLog")),
                bom,
                outputName);
    }
}
