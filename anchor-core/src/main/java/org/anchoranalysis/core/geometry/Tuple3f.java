package org.anchoranalysis.core.geometry;

import java.io.Serializable;

import org.anchoranalysis.core.arithmetic.FloatUtilities;

public class Tuple3f implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected float x = 0.0f;
	protected float y = 0.0f;
	protected float z = 0.0f;
		
	public void setValueByDimension( int dimIndex, float val ) {
		switch( dimIndex ) {
		case 0:
			this.x = val;
			break;
		case 1:
			this.y = val;
			break;
		case 2:
			this.z = val;
			break;
		default:
			assert false;
		}

	}
	
	public float getValueByDimension( int dimIndex ) {
		switch( dimIndex ) {
		case 0:
			return x;
		case 1:
			return y;
		case 2:
			return z;
		default:
			assert false;
			return 0;
		}
	}
	
	public float distanceSquared( Point3f pnt ) {
		float sx = this.x - pnt.x;
		float sy = this.y - pnt.y;
		float sz = this.z - pnt.z;
		return (sx*sx) + (sy*sy) + (sz*sz);
	}
	
	public double distance( Point3f pnt ) {
		return Math.sqrt( distanceSquared(pnt) );
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
	
	@Override
	public String toString() {
		return String.format("[%f,%f,%f]",x,y,z);
	}
	
	
	@Override
	public boolean equals( Object obj ) {
		if (this == obj) {
			return true;
		}
	    if (!(obj instanceof Tuple3f)) {
	        return false;
	    }
	    Tuple3f objCast = (Tuple3f) obj;
	    
	    if (!FloatUtilities.areEqual(x, objCast.x)) {
	    	return false;
	    }
	    
	    if (!FloatUtilities.areEqual(y, objCast.y)) {
	    	return false;
	    }
	    
	    if (!FloatUtilities.areEqual(z, objCast.z)) {
	    	return false;
	    }
	    
	    return true;
	}
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}
}
