/* (C)2020 */
package org.anchoranalysis.image.io.objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Paths to identify objects in the HDF5 for object-masks */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HDF5PathHelper {

    /** Attribute for x dimension of extent */
    public static final String EXTENT_X = "x";

    /** Attribute for y dimension of extent */
    public static final String EXTENT_Y = "y";

    /** Attribute for z dimension of extent */
    public static final String EXTENT_Z = "z";

    /**
     * Hardcoded string that identifies in the HDF5 file for where the object-collection is stored
     */
    public static final String OBJECTS_ROOT = "ObjMaskCollection";

    /** Adds seperators before and after the {@code OBJECTS_ROOT_PATH} */
    public static final String OBJECTS_ROOT_WITH_SEPERATORS = "/" + OBJECTS_ROOT + "/"; // NOSONAR

    public static String pathForObject(int index) {
        return String.format("%s/%08d", OBJECTS_ROOT_WITH_SEPERATORS, index);
    }
}
