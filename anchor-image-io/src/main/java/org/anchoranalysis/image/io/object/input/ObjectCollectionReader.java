/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.object.input;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.cache.CachedSupplier;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.checked.CheckedSupplier;
import org.anchoranalysis.core.serialize.DeserializationFailedException;
import org.anchoranalysis.core.serialize.Deserializer;
import org.anchoranalysis.image.voxel.object.ObjectCollection;

/**
 * Reads an {@link ObjectCollection} from the filesystem
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectCollectionReader {

    private static final Deserializer<ObjectCollection> TIFF_CORRECT_MISSING =
            new ReadObjectsFromTIFFDirectoryCorrectMissing();
    private static final Deserializer<ObjectCollection> HDF5 = new ReadObjectsFromHDF5();
    
    private static final String HDF5_EXTENSION = ".h5";

    /**
     * Reads an object-collection from a path (or path prefix) trying different methods to read the
     * objects.
     *
     * <p>The following order is used to look for an object-mask collection:
     *
     * <ol>
     *   <li>If path ends in
     *       <pre>.h5</pre>
     *       it is read as a HDF5 object-mask collection.
     *   <li>Otherwise,
     *       <pre>.h5</pre>
     *       is suffixed, and if this path exists, it is read as a HDF5 object-mask collection.
     *   <li>Otherwise, the path is assumed to be a directory, and this is read as a TIFF-directory
     *       with serialized bounding-boxes.
     * </ol>
     *
     * <p>In the case of 3, if the path does not exist, but it is the subpath of an {@link
     * ObjectCollection} directory which does then a special case occurs. An empty {@code
     * ObjectCollection} is returned.
     *
     * @param path path or (or path missing a
     *     <pre>.h5</pre>
     *     extension) used to search for an object-collection using the rules above
     * @return the object-collection read from this path.
     * @throws DeserializationFailedException if no objects are found at this path, or anything else
     *     prevents their deserialization.
     */
    public static ObjectCollection createFromPath(Path path) throws DeserializationFailedException {

        // 1. First check if has a file extension HDF5
        if (hasHdf5Extension(path)) {
            if (path.toFile().exists()) {
                return HDF5.deserialize(path);
            } else {
                throw new DeserializationFailedException("File not found at " + path);
            }
        }

        // 2. Suffix a .h5 and see if the file exists
        Path suffixed = addHdf5Extension(path);
        if (suffixed.toFile().exists()) {
            return HDF5.deserialize(suffixed);
        }

        // 3. Treat as a folder of TIFFs
        if (path.toFile().exists()) {
            return TIFF_CORRECT_MISSING.deserialize(path);
        } else {
            throw new DeserializationFailedException(
                    "Neither at HD5 file nor a directory of object TIFFs can be found for: "
                            + path);
        }
    }

    public static CheckedSupplier<ObjectCollection, OperationFailedException> createFromPathCached(
            CheckedSupplier<Path, OperationFailedException> path) {
        return CachedSupplier.cache(
                () -> {
                    try {
                        return createFromPath(path.get());
                    } catch (DeserializationFailedException e) {
                        throw new OperationFailedException(e);
                    }
                });
    }

    public static boolean hasHdf5Extension(Path path) {
        return path.toString().toLowerCase().endsWith(HDF5_EXTENSION);
    }

    private static Path addHdf5Extension(Path path) {
        return path.resolveSibling(path.getFileName() + HDF5_EXTENSION);
    }
}
