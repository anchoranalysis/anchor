/* (C)2020 */
package org.anchoranalysis.image.voxel.datatype;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

public class IncorrectVoxelDataTypeException extends AnchorFriendlyRuntimeException {

    /** */
    private static final long serialVersionUID = 1L;

    public IncorrectVoxelDataTypeException(String msg) {
        super(msg);
    }

    public IncorrectVoxelDataTypeException(Throwable exc) {
        super(exc);
    }
}
