/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.toint;

import java.nio.IntBuffer;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertTo;

public abstract class ConvertToInt extends ConvertTo<IntBuffer> {

    public ConvertToInt() {
        super(VoxelBoxWrapper::asInt);
    }
}
