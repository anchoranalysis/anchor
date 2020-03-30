package org.anchoranalysis.anchor.mpp.mark;

import org.anchoranalysis.core.geometry.Point2d;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.orientation.Orientation2D;
import org.anchoranalysis.image.points.BoundingBoxFromPoints;
import org.anchoranalysis.math.rotation.RotationMatrix;

/**
 * A two-dimensional bounding-box rotated at arbitrary angle in XY plane around a point.
 * 
 * <p>Axis-aligned bounding boxes are also supported by fixing orientation.</p>
 * 
 * @author owen
 *
 */
public class MarkRotatableBoundingBox extends MarkAbstractPosition {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START mark state

	// Note that internally three-dimensional points are used instead of two-dimensional as it
	//  fits more nicely with the rotation matrices (at the cost of some extra computation).
	
	/** Add to orientation to get the left-point and bottom-point (without rotation) */ 
	private Point3d distToLeftBottom;
	
	/** Add to orientation to get the right-point and top-point (without rotation) */
	private Point3d distToRightTop;
	
	private Orientation2D orientation;
	// END mark state
	
	// START internal objects
	private RotationMatrix rotMatrix;
	// END internal objects
	
	public MarkRotatableBoundingBox() {
		this.update( new Point2d(0,0), new Point2d(0,0), new Orientation2D() );
	}
	
	@Override
	public byte evalPntInside(Point3d pt) {
		// TODO populate with logic to find points inside
		assert(false);
		return 0;
	}
	
	public void update( Point2d distToLeftBottom, Point2d distToRightTop, Orientation2D orientation ) {
		
		this.rotMatrix = orientation.createRotationMatrix();
		update(
			convert3d(distToLeftBottom),
			convert3d(distToRightTop),
			orientation
		);
	}
		
	/** Internal version with Point3d */
	private void update( Point3d distToLeftBottom, Point3d distToRightTop, Orientation2D orientation ) {
		this.distToLeftBottom = distToLeftBottom;
		this.distToRightTop = distToRightTop;
		this.orientation = orientation;
		
		this.rotMatrix = orientation.createRotationMatrix();
	}

	@Override
	public BoundingBox bboxAllRegions(ImageDim bndScene) {
		
		Point3d leftBottomRot = rotateAddPos(distToLeftBottom);
		Point3d rightTopRot = rotateAddPos(distToRightTop);
		
		return BoundingBoxFromPoints.forTwoPoints(
			leftBottomRot,				
			rightTopRot
		);
	}
	
	@Override
	public BoundingBox bbox(ImageDim bndScene, int regionID) {
		return bboxAllRegions(bndScene);
	}
	
	@Override
	public double volume(int regionID) {
		// The volume is invariant to rotation
		double width = distToRightTop.getX() - distToLeftBottom.getX();
		double height = distToRightTop.getY() - distToLeftBottom.getY();
		return width * height;
	}

	@Override
	public Mark duplicate() {
		MarkRotatableBoundingBox out = new MarkRotatableBoundingBox();
		out.update(
			distToLeftBottom,
			distToRightTop,
			orientation.duplicate()
		);
		return out;
	}

	@Override
	public int numRegions() {
		return 1;
	}

	@Override
	public String getName() {
		return "rotatableBoundingBox";
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int numDims() {
		return 2;
	}
	
	private static Point3d convert3d( Point2d pnt ) {
		return new Point3d(pnt.getX(), pnt.getY(), 0);
	}

	/** Rotates a position and adds the current position afterwards */
	private Point3d rotateAddPos( Point3d pnt ) {
		Point3d out = rotMatrix.calcRotatedPoint(pnt);
		out.add( getPos() );
		return out;
	}
}
