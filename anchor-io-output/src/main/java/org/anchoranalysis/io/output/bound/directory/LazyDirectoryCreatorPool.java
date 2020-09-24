/*-
 * #%L
 * anchor-io-output
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.io.output.bound.directory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.filepath.prefixer.PathDifferenceFromBase;
import org.anchoranalysis.io.output.writer.WriterExecuteBeforeEveryOperation;
import lombok.Getter;

/**
 * A pool that memoizes and stores an associated {@link LazyDirectoryCreator} for directories.
 * 
 * <p>Directories are first normalized, and all must exist within a particular {@code rootDirectory}.
 *
 * @author Owen Feehan
 */
class LazyDirectoryCreatorPool {

    // START REQUIRED ARGUMENTS
    /** If true, any existing directory at the intended path for creation, is first deleted. If false, an exception is thrown in this circumstance. */
    private final boolean deleteExisting;

    /** The root directory in which <i>all</i> memoized directories must exist. */
    @Getter private final Path rootDirectory;
    // END REQUIRED ARGUMENTS

    // Cache all directories created by Path
    private Map<Path, LazyDirectoryCreator> map = new HashMap<>();

    /**
     * Creates for a root-direction
     *
     * @param rootDirectory a root directory, in which all paths subsequently passed to {@link
     *     #getOrCreate} should reside.
     * @param deleteExisting if true, deletes any existing folder that exists in paths we create. if
     *     false, an error is thrown if an existing directory exists.
     */
    public LazyDirectoryCreatorPool(Path rootDirectory, boolean deleteExisting) {
        this.rootDirectory = rootDirectory.normalize();
        this.deleteExisting = deleteExisting;
    }

    /**
     * Retrieves an existing element from the map or creates if it doesn't already exist
     *
     * @param directory the full-path of the directory in the map
     * @param opBefore an operation to execute before creating the directory (if it doesn't already
     *     exist)
     * @return either an existing element if it exists or a newly created element
     */
    public LazyDirectoryCreator getOrCreate(
            Path directory, Optional<WriterExecuteBeforeEveryOperation> opBefore)
            throws GetOperationFailedException {

        if (directory.equals(rootDirectory)) {
            return getRootCreator(opBefore);
        }

        return processEachDirectoryComponent(differenceFromRoot(directory), opBefore);
    }

    /**
     * Retrieves an existing element from the map
     *
     * @param directory the full-path of the directory in the map
     * @return an existing element if it exists
     * @throws GetOperationFailedException if the directory-path does not reside within the {@code
     *     rootDirectory}.
     */
    public synchronized Optional<LazyDirectoryCreator> get(Path directory)
            throws GetOperationFailedException {
        return Optional.ofNullable(map.get(differenceFromRoot(directory)));
    }

    /** A directory-creator for the root directory */
    private LazyDirectoryCreator getRootCreator(
            Optional<WriterExecuteBeforeEveryOperation> opBefore) {
        return getOrCreate(Paths.get(""), rootDirectory, opBefore);
    }

    /**
     * Iterate through each component of the path, to ensure a separate initializer exists for each
     * directory in our path (within the rootDirectory).
     *
     * @param pathDifference
     * @param opBefore
     * @return
     */
    private LazyDirectoryCreator processEachDirectoryComponent(
            Path pathDifference, Optional<WriterExecuteBeforeEveryOperation> opBefore) {

        Iterator<Path> iterator = pathDifference.iterator();

        // Successively changed as we iterate over directories, but the first directory calls the
        // parameter opBefore
        Optional<WriterExecuteBeforeEveryOperation> opBeforeRunning = opBefore;

        // Successively changed as we iterate over directories
        LazyDirectoryCreator lazyDirectory = null;
        Path key = null;

        while (iterator.hasNext()) {

            Path next = iterator.next();

            // Keep successively building a relative directory that (that acts as a key in the map)
            if (key == null) {
                key = next;
            } else {
                key = key.resolve(next);
            }

            lazyDirectory = getOrCreate(key, rootDirectory.resolve(key), opBeforeRunning);

            // The current directory becomes the opBefore of the next element
            opBeforeRunning = Optional.of(lazyDirectory);
        }

        // The iterator will always have at least one value, otherwise an exception will have
        // already been thrown
        // So in practice, the return value will never be null
        return lazyDirectory;
    }

    private synchronized LazyDirectoryCreator getOrCreate(
            Path key, Path directoryFull, Optional<WriterExecuteBeforeEveryOperation> opBefore) {
        return map.computeIfAbsent(
                key,
                // The creator is bound to the full path not the partial path in the key
                path -> new LazyDirectoryCreator(directoryFull, deleteExisting, opBefore));
    }

    private Path differenceFromRoot(Path path) throws GetOperationFailedException {
        try {
            return PathDifferenceFromBase.differenceFrom(rootDirectory, path.normalize())
                    .combined();
        } catch (AnchorIOException e) {
            throw new GetOperationFailedException(path.toString(), e);
        }
    }
}
