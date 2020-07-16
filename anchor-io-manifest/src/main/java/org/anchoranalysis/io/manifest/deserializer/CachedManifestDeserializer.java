/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer;

import java.io.File;
import org.anchoranalysis.core.cache.LRUCache;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.ManifestRecorder;

public class CachedManifestDeserializer implements ManifestDeserializer {

    private LRUCache<File, ManifestRecorder> cachedItems;

    // Cache, last-used gets deleted when the cacheSize is reached
    public CachedManifestDeserializer(final ManifestDeserializer delegate, int cacheSize) {
        super();
        cachedItems = new LRUCache<>(cacheSize, delegate::deserializeManifest);
    }

    @Override
    public ManifestRecorder deserializeManifest(File file) throws DeserializationFailedException {
        try {
            return cachedItems.get(file);
        } catch (GetOperationFailedException e) {
            throw new DeserializationFailedException(e);
        }
    }
}
