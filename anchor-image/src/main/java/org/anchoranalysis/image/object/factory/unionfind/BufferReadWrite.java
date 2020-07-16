/* (C)2020 */
package org.anchoranalysis.image.object.factory.unionfind;

import java.nio.Buffer;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;

abstract class BufferReadWrite<T extends Buffer> {

    protected abstract boolean isBufferOn(
            T buffer, int offset, BinaryValues bv, BinaryValuesByte bvb);

    protected abstract void putBufferCnt(T buffer, int offset, int cnt);
}
