/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer;

import java.io.File;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.ManifestRecorder;

public interface ManifestDeserializer {

    ManifestRecorder deserializeManifest(File file) throws DeserializationFailedException;
}
