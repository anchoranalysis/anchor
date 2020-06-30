package org.anchoranalysis.anchor.mpp.mark.conic.bounds;

import org.anchoranalysis.anchor.mpp.bean.bound.Bound;
import org.anchoranalysis.anchor.mpp.bean.bound.BoundUnitless;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.orientation.Orientation;
import org.anchoranalysis.image.orientation.Orientation2D;

/**
 * Creates a randomly-generated orientation in 2D by uniformally sampling a scalar rotation angle
 * 
 * @author Owen Feehan
  */
public class RotationBounds2D extends RotationBounds {

	// START BEAN PROPERTIES
	@BeanField
	private Bound rotationAngle = new BoundUnitless(0, 2 * Math.PI);
	// END BEAN PROPERTIES

	@Override
	public Orientation randomOrientation(RandomNumberGenerator re, ImageResolution res) {
		return new Orientation2D( getRotationAngle().rslv(res, false).randOpen(re) );
	}	
	@Override
	public String getBeanDscr() {
		return String.format("%s, rotation=(%s)", getBeanName(), rotationAngle.toString() );
	}

	public Bound getRotationAngle() {
		return rotationAngle;
	}

	public void setRotationAngle(Bound rotationAngle) {
		this.rotationAngle = rotationAngle;
	}
}
