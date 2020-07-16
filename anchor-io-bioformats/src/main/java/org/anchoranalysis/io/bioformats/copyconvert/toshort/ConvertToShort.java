/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.toshort;

import java.nio.ShortBuffer;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertTo;

public abstract class ConvertToShort extends ConvertTo<ShortBuffer> {

    public ConvertToShort() {
        super(VoxelBoxWrapper::asShort);
    }
}
