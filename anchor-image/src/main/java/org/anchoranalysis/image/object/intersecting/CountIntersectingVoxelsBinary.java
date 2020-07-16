/* (C)2020 */
package org.anchoranalysis.image.object.intersecting;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;

/**
 * Counts the number of intersecting voxels where each buffer is encoded as Binary-Values
 *
 * @author Owen Feehan
 */
public class CountIntersectingVoxelsBinary extends CountIntersectingVoxels {

    private byte byteOn1;
    private byte byteOn2;

    public CountIntersectingVoxelsBinary(BinaryValuesByte bvb1, BinaryValuesByte bvb2) {
        super();
        this.byteOn1 = bvb1.getOnByte();
        this.byteOn2 = bvb2.getOnByte();
    }

    @Override
    protected int countIntersectingVoxels(
            ByteBuffer buffer1, ByteBuffer buffer2, IntersectionBBox bbox) {

        int cnt = 0;
        for (int y = bbox.y().min(); y < bbox.y().max(); y++) {
            int yOther = y + bbox.y().rel();

            for (int x = bbox.x().min(); x < bbox.x().max(); x++) {
                int xOther = x + bbox.x().rel();

                byte posCheck = buffer1.get(bbox.e1().offset(x, y));
                byte posCheckOther = buffer2.get(bbox.e2().offset(xOther, yOther));

                if (posCheck == byteOn1 && posCheckOther == byteOn2) {
                    cnt++;
                }
            }
        }
        return cnt;
    }
}
