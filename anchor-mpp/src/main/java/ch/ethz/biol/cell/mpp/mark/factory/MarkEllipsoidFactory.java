package ch.ethz.biol.cell.mpp.mark.factory;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.conic.MarkEllipsoid;

/*
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import org.anchoranalysis.bean.annotation.BeanField;

public class MarkEllipsoidFactory extends MarkFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private double shellRad = 0.1;
	// END BEAN PROPERTIES
	
	@Override
	public Mark create() {
		MarkEllipsoid mark = new MarkEllipsoid();
		mark.setShellRad(shellRad);
		return mark;
	}

	public double getShellRad() {
		return shellRad;
	}

	public void setShellRad(double shellRad) {
		this.shellRad = shellRad;
	}
	
	
//	public static MarkEllipsoid createMarkEllipsoidMoments( ObjMask om, boolean suppressZCovariance ) {
//		
//		SecondMomentMatrix	moments = SecondMomentMatrixFactory.createSecondMomentsForObjMaskPoints(om, suppressZCovariance);
//		
//		double radMaj = moments.get(0).eigenvalueNormalizedAsAxisLength()/2;
//		double radMinor = moments.get(1).eigenvalueNormalizedAsAxisLength()/2;
//		double radMostMinor = moments.get(2).eigenvalueNormalizedAsAxisLength()/2;
//		
//		if (radMaj<1.0) {
//			radMaj = 1.0;
//		}
//		if (radMinor<1.0) {
//			radMinor = 1.0;
//		}
//		if (radMostMinor<1.0) {
//			radMostMinor = 1.0;
//		}
//		
//		assert(radMaj>0.0);
//		assert(radMinor>0.0);
//		assert(radMostMinor>0.0);
//		
//		Point3d posNew = new Point3d(
//				moments.getMean(0) + om.getBoundingBox().getCrnrMin().getX(),
//				moments.getMean(1) + om.getBoundingBox().getCrnrMin().getY(),
//				moments.getMean(2) + om.getBoundingBox().getCrnrMin().getZ()
//			);
//		
//		RotationMatrix rotMatrix = RotationMatrix.createFrom3Vecs(
//			moments.get(0).getEigenvector(),
//			moments.get(1).getEigenvector(),
//			moments.get(2).getEigenvector()
//		);
//		
//		//RotationMatrix rotMatrix = RotationMatrix.createToAlignXAxisWith( moments.get(2).getEigenVector(), moments.get(1).getEigenVector() );
//		
//		Orientation orientation = new OrientationRotationMatrix(rotMatrix); 
//		
//		MarkEllipsoid me = new MarkEllipsoid();
//		me.setMarksExplicit( posNew, orientation, new Point3d( radMaj, radMinor, radMostMinor ) );
//		return me;
//	}

}
