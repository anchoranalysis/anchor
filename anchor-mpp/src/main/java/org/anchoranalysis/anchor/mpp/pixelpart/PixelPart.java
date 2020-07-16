/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pixelpart;

import org.anchoranalysis.anchor.mpp.pixelpart.factory.PixelPartFactory;

/**
 * A partition of pixels
 *
 * @author Owen Feehan
 * @param <T> part-type
 */
public interface PixelPart<T> {

    // Should only be used read-only, if we want to maintain integrity with the combined list
    T getSlice(int sliceID);

    void addForSlice(int sliceID, int val);

    // Should only be used read-only
    T getCombined();

    void cleanUp(PixelPartFactory<T> factory);

    int numSlices();
}
