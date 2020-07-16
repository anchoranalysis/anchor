/* (C)2020 */
package org.anchoranalysis.anchor.mpp.pixelpart.factory;

import org.anchoranalysis.anchor.mpp.pixelpart.PixelPart;

/**
 * @author Owen Feehan
 * @param <T> part-type
 */
public interface PixelPartFactory<T> {

    PixelPart<T> create(int numSlices);

    void addUnused(T part);
}
