package org.anchoranalysis.anchor.mpp.bean.proposer.radii;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.orientation.Orientation;

import ch.ethz.biol.cell.mpp.mark.EllipseBounds;
import ch.ethz.biol.cell.mpp.mark.Mark;
import ch.ethz.biol.cell.mpp.mark.MarkBounds;
import ch.ethz.biol.cell.mpp.mark.MarkEllipse;

public class EllipseBoundsProposer extends RadiiProposer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8713626984466863980L;

	@Override
	public boolean isCompatibleWith(Mark testMark) {
		return testMark instanceof MarkEllipse;
	}

	@Override
	public Point3d propose(Point3d pos, MarkBounds markBounds,
			RandomNumberGenerator re, ImageDim bndScene, Orientation orientation,
			ErrorNode proposerFailureDescription) {
	
		EllipseBounds eb = (EllipseBounds) markBounds;
		
		Point3d radii = new Point3d();
		radii.setX( eb.getRadius().rslv( bndScene.getRes(), false ).randOpen( re ) );
		radii.setY( eb.getRadius().rslv( bndScene.getRes(), false ).randOpen( re ) );
		radii.setZ(0);
		return radii;
	}

}
