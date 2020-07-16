/* (C)2020 */
package org.anchoranalysis.io.output.bound;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;

/**
 * Memoizes LazyDirectoryInit creation for particular outputDirectory (normalized)
 *
 * <p>The {@code parent} parameter is always assumed to be uniform for a given outputDirectory
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public class LazyDirectoryFactory {

    // START REQUIRED ARGUMENTS
    private final boolean delExistingFolder;
    // END REQUIRED ARGUMENTS

    // Cache all directories created by Path
    private Map<Path, WriterExecuteBeforeEveryOperation> map = new HashMap<>();

    public synchronized WriterExecuteBeforeEveryOperation createOrReuse(
            Path outputDirectory, Optional<WriterExecuteBeforeEveryOperation> parent) {
        // So that we are always referring to a canonical output-directory path
        Path outputDirectoryNormalized = outputDirectory.normalize();
        return map.computeIfAbsent(
                outputDirectoryNormalized,
                path -> new LazyDirectoryInit(path, delExistingFolder, parent));
    }
}
