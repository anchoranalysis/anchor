/* (C)2020 */
package org.anchoranalysis.io.manifest;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.cache.CachedOperation;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.deserializer.ManifestDeserializer;

@AllArgsConstructor
public class ManifestRecorderFile
        extends CachedOperation<ManifestRecorder, OperationFailedException> {

    private final File file;
    private final ManifestDeserializer manifestDeserializer;

    @Override
    protected ManifestRecorder execute() throws OperationFailedException {
        try {
            if (!file.exists()) {
                throw new OperationFailedException(
                        String.format("File %s cannot be found", file.getPath()));
            }
            return manifestDeserializer.deserializeManifest(file);
        } catch (DeserializationFailedException e) {
            throw new OperationFailedException(e);
        }
    }

    public Path getRootPath() {
        // Returns the path of the root of the manifest file (or what it will become)
        return Paths.get(file.getParent());
    }
}
