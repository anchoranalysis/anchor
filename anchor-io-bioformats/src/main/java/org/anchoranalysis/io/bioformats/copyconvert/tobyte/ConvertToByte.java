/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert.tobyte;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.io.bioformats.copyconvert.ConvertTo;

public abstract class ConvertToByte extends ConvertTo<ByteBuffer> {

    public ConvertToByte() {
        super(VoxelBoxWrapper::asByte);
    }
}
