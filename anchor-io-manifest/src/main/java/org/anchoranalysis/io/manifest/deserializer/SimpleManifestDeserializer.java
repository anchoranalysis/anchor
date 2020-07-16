/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer;

import java.io.File;
import org.anchoranalysis.io.bean.deserializer.ObjectInputStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.ManifestRecorder;

public class SimpleManifestDeserializer implements ManifestDeserializer {

    @Override
    public ManifestRecorder deserializeManifest(File file) throws DeserializationFailedException {

        ManifestRecorder manifest =
                new ObjectInputStreamDeserializer<ManifestRecorder>().deserialize(file.toPath());
        manifest.init(file.toPath().getParent());
        return manifest;
    }
}
