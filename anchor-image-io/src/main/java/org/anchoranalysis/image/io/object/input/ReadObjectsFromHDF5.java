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

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import ch.systemsx.cisd.hdf5.exceptions.HDF5FileNotFoundException;
import com.google.common.base.Preconditions;
import java.nio.file.Path;
import java.util.List;
import org.anchoranalysis.core.serialize.DeserializationFailedException;
import org.anchoranalysis.core.serialize.Deserializer;
import org.anchoranalysis.core.time.OperationContext;
import org.anchoranalysis.image.io.object.HDF5PathHelper;
import org.anchoranalysis.image.io.object.output.hdf5.HDF5ObjectsGenerator;
import org.anchoranalysis.image.io.object.output.hdf5.ObjectCollectionWriter;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectCollectionFactory;

/**
 * Reads a {@link ObjectMaskCollection} that has been serialized to a HDF5 file.
 *
 * <p>The serialized file should be in the form created by {@link ObjectCollectionWriter}.
 *
 * @author Owen Feehan
 */
class ReadObjectsFromHDF5 implements Deserializer<ObjectCollection> {

    @Override
    public ObjectCollection deserialize(Path path, OperationContext context)
            throws DeserializationFailedException {

        try (IHDF5Reader reader = HDF5Factory.openForReading(path.toString())) {

            return readObjects(reader, HDF5PathHelper.OBJECTS_ROOT_WITH_SEPERATORS);

        } catch (HDF5FileNotFoundException e) {
            throw new DeserializationFailedException(
                    String.format("HDF5 file not found at %s", path));
        } catch (Exception e) {
            throw new DeserializationFailedException(
                    String.format(
                            "An error occurred while reading HDF5 at %s with rootPath=%s",
                            path, HDF5PathHelper.OBJECTS_ROOT_WITH_SEPERATORS),
                    e);
        }
    }

    /**
     * Read all objects from the HDF5 file.
     *
     * @param reader an opened HDF5 file.
     * @param rootPath a path in the HDF5 file, where the object-collection exists. This path should
     *     always end in a forward-slash.
     * @return the deserialized {@link ObjectCollection}.
     * @throws DeserializationFailedException if the data cannot be successfully read, or has has
     *     unexpected structure.
     */
    private ObjectCollection readObjects(IHDF5Reader reader, String rootPath)
            throws DeserializationFailedException {
        Preconditions.checkArgument(rootPath.endsWith("/"));

        // First check the number of objects expected
        // if the the rootPath exists in the HDF5, if not, it's an indication that there's no
        // objects present
        int numberObjects =
                reader.uint32().getAttr("/", HDF5ObjectsGenerator.NUMBER_OBJECTS_ATTRIBUTE_NAME);
        if (numberObjects == 0) {
            return ObjectCollectionFactory.empty();
        }

        ObjectCollection out = readObjectsNoCheck(reader, rootPath);

        if (out.size() != numberObjects) {
            throw new DeserializationFailedException(
                    String.format(
                            "An error occurred deserializing HDF5, where %d objects were expected but %d were read.",
                            numberObjects, out.size()));
        }
        return out;
    }

    /**
     * Reads the objects without doing any check on the total number of objects expected or
     * received.
     */
    private ObjectCollection readObjectsNoCheck(IHDF5Reader reader, String rootPath) {
        List<String> groups = reader.object().getAllGroupMembers(rootPath);
        return ObjectCollectionFactory.mapFrom(
                groups, groupName -> ObjectMaskHDF5Reader.readObject(reader, rootPath + groupName));
    }
}
