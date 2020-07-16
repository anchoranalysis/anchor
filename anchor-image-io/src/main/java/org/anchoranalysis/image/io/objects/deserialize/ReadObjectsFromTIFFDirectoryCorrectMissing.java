/* (C)2020 */
package org.anchoranalysis.image.io.objects.deserialize;

import java.nio.file.Path;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectCollectionFactory;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

class ReadObjectsFromTIFFDirectoryCorrectMissing implements Deserializer<ObjectCollection> {

    private static final String OBJECT_DIRECTORY_NAME = "objects";

    @Override
    public ObjectCollection deserialize(Path path) throws DeserializationFailedException {
        // Work around to tell the difference between a deliberately abandoned object-collection and
        // an empty set
        if (isMissingButLooksLikeCollection(path)) {
            return ObjectCollectionFactory.empty();
        } else {
            return new ReadObjectsFromTIFFDirectory().deserialize(path);
        }
    }

    /** If the path is missing, but OBJECT_DIRECTORY_NAME is found as a parent-component */
    private static boolean isMissingButLooksLikeCollection(Path folderPath) {
        if (!folderPath.toFile().exists()) {

            Path parent = folderPath.getParent();

            // Check if one folder up exists and is equal to OBJECT_DIRECTORY_NAME
            if (parent.toFile().exists() && namedAsObjectDirectory(parent)) {
                return true;
            }
        }
        return false;
    }

    private static boolean namedAsObjectDirectory(Path path) {
        return path.getName(path.getNameCount() - 1).toString().equals(OBJECT_DIRECTORY_NAME);
    }
}
