package org.anchoranalysis.anchor.mpp.pixelpart.factory;

import org.anchoranalysis.anchor.mpp.pixelpart.PixelPart;


/**
 * 
 * @author owen
 *
 * @param <T> part-type
 */
public abstract class PixelPartFactory<T> {

	public abstract PixelPart<T> create( int numSlices );
	
	public abstract void addUnused( T part );
}
