/* (C)2020 */
package org.anchoranalysis.image.object.factory.unionfind;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;

final class ReadWriteByte extends BufferReadWrite<ByteBuffer> {

    @Override
    protected boolean isBufferOn(
            ByteBuffer buffer, int offset, BinaryValues bv, BinaryValuesByte bvb) {
        return buffer.get(offset) == bvb.getOnByte();
    }

    @Override
    protected void putBufferCnt(ByteBuffer buffer, int offset, int cnt) {
        buffer.put(offset, (byte) cnt);
    }
}
