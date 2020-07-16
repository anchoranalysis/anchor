/* (C)2020 */
package org.anchoranalysis.image.object.factory.unionfind;

import java.nio.IntBuffer;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;

final class ReadWriteInt extends BufferReadWrite<IntBuffer> {

    @Override
    protected boolean isBufferOn(
            IntBuffer buffer, int offset, BinaryValues bv, BinaryValuesByte bvb) {
        return buffer.get(offset) == bv.getOnInt();
    }

    @Override
    protected void putBufferCnt(IntBuffer buffer, int offset, int cnt) {
        buffer.put(offset, cnt);
    }
}
