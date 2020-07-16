/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pxlmark;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.anchoranalysis.feature.nrg.NRGStack;

class BufferArrList {

    private ArrayList<ByteBuffer> delegate = new ArrayList<>();

    public boolean add(ByteBuffer e) {
        return delegate.add(e);
    }

    public void init(NRGStack stack, int z) {

        for (int c = 0; c < stack.getNumChnl(); c++) {
            ByteBuffer bb =
                    stack.getChnl(c)
                            .getVoxelBox()
                            .asByte()
                            .getPlaneAccess()
                            .getPixelsForPlane(z)
                            .buffer();
            delegate.add(bb);
        }
    }

    public ByteBuffer get(int index) {
        return delegate.get(index);
    }
}
