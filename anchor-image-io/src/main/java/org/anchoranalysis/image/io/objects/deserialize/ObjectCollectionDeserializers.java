/* (C)2020 */
package org.anchoranalysis.image.io.objects.deserialize;

import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.bean.deserializer.Deserializer;

/** Entry point to deserializers */
public class ObjectCollectionDeserializers {

    private ObjectCollectionDeserializers() {}

    public static final Deserializer<ObjectCollection> TIFF_CORRECT_MISSING =
            new ReadObjectsFromTIFFDirectoryCorrectMissing();
    public static final Deserializer<ObjectCollection> HDF5 = new ReadObjectsFromHDF5();
}
