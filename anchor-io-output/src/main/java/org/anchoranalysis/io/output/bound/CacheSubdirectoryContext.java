/* (C)2020 */
package org.anchoranalysis.io.output.bound;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.manifest.ManifestFolderDescription;

/**
 * Caches sub-directories as they are created, so as to reuse the BoundIOContext, without creating
 * duplicate manifest entries.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class CacheSubdirectoryContext {

    /** the context of the directory in which subdirectories may be created */
    private final BoundIOContext parentContext;

    /** A description to use for every created folder */
    private final ManifestFolderDescription manifestFolderDescription;

    private Map<Optional<String>, BoundIOContext> mapOutputManagers = new HashMap<>();

    /**
     * Gets (from the cache if it's already there) subdirectory for a given-name
     *
     * @param subdirectoryName the sub-directory name. if not set, then the parentContext is
     *     returned instead.
     * @return
     */
    public BoundIOContext get(Optional<String> subdirectoryName) {
        return mapOutputManagers.computeIfAbsent(
                subdirectoryName,
                key -> parentContext.maybeSubdirectory(key, manifestFolderDescription));
    }
}
