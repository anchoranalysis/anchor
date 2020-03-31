package org.anchoranalysis.image.objmask;

/**
 * Calculates overlap between object-masks
 * 
 * @author owen
 *
 */
public class ObjMaskOverlap {
	
	public static double calcOverlapRatio( ObjMask objA, ObjMask objB, ObjMask objMerged ) {
		
		int intersectingPixels = objA.countIntersectingPixels(objB);
		if (intersectingPixels==0) {
			return 0;
		}
		
		int vol = objMerged.numPixels();
		
		return ((double) intersectingPixels) / vol;
	}
}
