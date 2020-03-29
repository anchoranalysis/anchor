
package org.anchoranalysis.anchor.mpp.pixelpart;

import org.anchoranalysis.anchor.mpp.pixelpart.factory.PixelPartFactory;

/**
 * A partition of pixels
 * 
 * @author owen
 *
 * @param <T> part-type
 */
public abstract class PixelPart<T> {
	
	// Should only be used read-only, if we want to maintain integrity with the combined list
	public abstract T getSlice( int sliceID );
	
	public abstract void addForSlice( int sliceID, int val );

	// Should only be used read-only
	public abstract T getCombined();
	
	public abstract void cleanUp( PixelPartFactory<T> factory );
	
	public abstract int numSlices();
}
