package org.anchoranalysis.core.geometry;

import java.io.Serializable;

import org.anchoranalysis.core.arithmetic.FloatUtilities;
import org.anchoranalysis.core.axis.AxisType;
import org.anchoranalysis.core.axis.AxisTypeConverter;
import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

public class Tuple3f implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected float x = 0.0f;
	protected float y = 0.0f;
	protected float z = 0.0f;
		
	public final float getValueByDimension(int dimIndex) {
		if (dimIndex==0) {
			return x;
		} else if (dimIndex==1) {
			return y;
		} else if (dimIndex==2) {
			return z;
		} else {
			throw new AnchorFriendlyRuntimeException(AxisTypeConverter.INVALID_AXIS_INDEX);
		}
	}
	
	public final float getValueByDimension( AxisType axisType ) {
		switch( axisType ) {
		case X:
			return x;
		case Y:
			return y;
		case Z:
			return z;
		default:
			assert false;
			throw new AnchorFriendlyRuntimeException(AxisTypeConverter.UNKNOWN_AXIS_TYPE);
		}
	}
	
	public final void setValueByDimension( int dimIndex, float val ) {
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
			throw new AnchorFriendlyRuntimeException(AxisTypeConverter.INVALID_AXIS_INDEX);
		}
	}
	
	public float distanceSquared( Point3f pnt ) {
		float sx = this.x - pnt.x;
		float sy = this.y - pnt.y;
		float sz = this.z - pnt.z;
		return (sx*sx) + (sy*sy) + (sz*sz);
	}
	
	public final double distance( Point3f pnt ) {
		return Math.sqrt( distanceSquared(pnt) );
	}

	public final float getX() {
		return x;
	}

	public final void setX(float x) {
		this.x = x;
	}

	public final float getY() {
		return y;
	}

	public final void setY(float y) {
		this.y = y;
	}

	public final float getZ() {
		return z;
	}

	public final void setZ(float z) {
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
