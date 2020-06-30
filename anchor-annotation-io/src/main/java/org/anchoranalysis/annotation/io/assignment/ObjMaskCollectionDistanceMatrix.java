package org.anchoranalysis.annotation.io.assignment;

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.object.ObjectCollection;

public class ObjMaskCollectionDistanceMatrix {

	// A two-dimensional array mapping objs1 to objs2
	private double[][] distanceArr;
	
	private ObjectCollection objs1;
	private ObjectCollection objs2;

	public ObjMaskCollectionDistanceMatrix(
		ObjectCollection objs1,
		ObjectCollection objs2,
		double[][] distanceArr
	) throws CreateException {
		super();
		
		this.objs1 = objs1;
		this.objs2 = objs2;
		this.distanceArr = distanceArr;
		
		if (objs1.isEmpty()) {
			throw new CreateException("objs1 must be non-empty");
		}
		
		if (objs2.isEmpty()) {
			throw new CreateException("objs2 must be non-empty");
		}
		
		if ((distanceArr.length != objs1.size()) || distanceArr[0].length != objs2.size()) {
			throw new CreateException("The distance-array has incorrect dimensions to match the objects");
		}		
	}
	
	public double getDistance( int indx1, int indx2 ) {
		return distanceArr[indx1][indx2];
	}

	public double[][] getDistanceArr() {
		return distanceArr;
	}

	public ObjectCollection getObjs1() {
		return objs1;
	}

	public ObjectCollection getObjs2() {
		return objs2;
	}

	public int sizeObjs1() {
		return objs1.size();
	}
	
	public int sizeObjs2() {
		return objs2.size();
	}
}
