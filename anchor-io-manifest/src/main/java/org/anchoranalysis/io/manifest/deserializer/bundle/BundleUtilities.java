/* (C)2020 */
package org.anchoranalysis.io.manifest.deserializer.bundle;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;
import org.anchoranalysis.io.manifest.file.FileWrite;
import org.anchoranalysis.io.manifest.folder.FolderWrite;
import org.anchoranalysis.io.manifest.match.FileWriteIndex;

public class BundleUtilities {

    private BundleUtilities() {
        // static access only
    }

    public static <T extends Serializable> Bundle<T> generateBundle(
            Deserializer<Bundle<T>> deserializer, FolderWrite rootFolder, int index)
            throws DeserializationFailedException {

        List<FileWrite> foundList = new ArrayList<>();

        String indexStr = Integer.toString(index);

        FileWriteIndex match = new FileWriteIndex(indexStr);
        rootFolder.findFile(foundList, match, true);

        if (foundList.size() != 1) {
            throw new IllegalArgumentException(String.format("Cannot find index %s", indexStr));
        }

        Bundle<T> bundle = deserializer.deserialize(foundList.get(0).calcPath());

        assert bundle != null;

        // We deserialize the file file, and create  new
        return bundle;
    }

    public static BundleParameters generateBundleParameters(
            Deserializer<BundleParameters> deserializer, FolderWrite rootFolder)
            throws DeserializationFailedException {

        // We construct a path
        Path path = rootFolder.calcPath().resolve("bundleParameters.ser");

        BundleParameters bundleParameters = deserializer.deserialize(path);

        assert bundleParameters != null;

        // We deserialize the file file, and create  new
        return bundleParameters;
    }
}
