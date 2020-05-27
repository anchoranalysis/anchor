package org.anchoranalysis.core.geometry;

import org.anchoranalysis.core.axis.AxisType;

/**
 * Read-only access to a tuple
 * 
 * @author Owen Feehan
 *
 */
public interface ReadableTuple3i {

	int getX();
	
	int getY();
	
	int getZ();
	
	int getValueByDimension(AxisType axisType);
	
	int getValueByDimension(int dimIndex);
	
	ReadableTuple3i duplicateChangeZ( int zNew );
}