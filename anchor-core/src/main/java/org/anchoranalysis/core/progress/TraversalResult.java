package org.anchoranalysis.core.progress;

import java.nio.file.Path;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor @Value
public class TraversalResult {

    /**
     * All the directories in the bottom-most depth that was traversed.These directories were
     * not traversed
     */
    private List<Path> leafDirectories;

    /**
     * All files in any directories that have been traversed.
     *
     * <p>Note that no files from {@code leafDirectories} are present as it was not yet traversed.
     */
    private List<Path> files;

    /**
     * The depth of directories processed, where 1 = root directory traversed, 2 = root
     * directory + one further level etc.
     */
    private int depth;
}