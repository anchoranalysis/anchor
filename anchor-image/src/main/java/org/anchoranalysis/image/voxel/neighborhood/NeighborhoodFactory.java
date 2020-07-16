package org.anchoranalysis.image.voxel.neighborhood;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Creates either a big voxel-neighborhood or a small voxel-neighborhood.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class NeighborhoodFactory {

	private static final Neighborhood BIG = new BigNeighborhood();
	private static final Neighborhood SMALL = new SmallNeighborhood();
	
	/**
	 * Gets an appropriate neighborhood
	 * 
	 * @param big if TRUE, a big neighborhood, if FALSE a small one
	 * @return the neighborhood
	 */
	public static Neighborhood of(boolean big) {
		return big ? BIG : SMALL;
	}
}
