/* (C)2020 */
package org.anchoranalysis.image.io.objects.deserialize;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5Reader;
import java.nio.file.Path;
import java.util.List;
import ncsa.hdf.hdf5lib.exceptions.HDF5FileNotFoundException;
import org.anchoranalysis.image.io.objects.GeneratorHDF5;
import org.anchoranalysis.image.io.objects.HDF5PathHelper;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.error.AnchorIOException;

class ReadObjectsFromHDF5 implements Deserializer<ObjectCollection> {

    private static final ObjectMaskHDF5Reader OBJECT_READER = new ObjectMaskHDF5Reader();

    @Override
    public ObjectCollection deserialize(Path path) throws DeserializationFailedException {

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
     * Read all objects
     *
     * @param reader
     * @param rootPath a path in the HDF5, NOTE it should always end in a forward-slash
     * @return
     * @throws DeserializationFailedException
     * @throws AnchorIOException
     */
    private ObjectCollection readObjects(IHDF5Reader reader, String rootPath)
            throws DeserializationFailedException {

        assert (rootPath.endsWith("/"));

        // First check the number of objects expected
        // if the the rootPath exists in the HDF5, if not, it's an indication that there's no
        // objects present
        int numberObjects =
                ObjectMaskHDF5Reader.extractIntAttr(
                        reader.uint32(), "/", GeneratorHDF5.NUM_OBJECTS_ATTR_NAME);
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
     * Reads the objects without doing any check on the total number of objects expected or received
     */
    private ObjectCollection readObjectsNoCheck(IHDF5Reader reader, String rootPath) {

        List<String> groups = reader.object().getAllGroupMembers(rootPath);
        return ObjectCollectionFactory.mapFrom(
                groups, groupName -> OBJECT_READER.apply(reader, rootPath + groupName));
    }
}
