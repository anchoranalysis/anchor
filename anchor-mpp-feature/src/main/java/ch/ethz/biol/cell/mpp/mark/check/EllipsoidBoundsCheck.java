package ch.ethz.biol.cell.mpp.mark.check;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.conic.EllipsoidUtilities;
import org.anchoranalysis.anchor.mpp.mark.conic.MarkEllipsoid;

/*-
 * #%L
 * anchor-mpp-feature
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

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

import ch.ethz.biol.cell.core.CheckMark;

public class EllipsoidBoundsCheck extends CheckMark {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7367208466737073130L;
	
	// START BEAN PROPERTIES
	
	// END BEAN PROPERTIES

	
	@Override
	public boolean check(Mark mark, RegionMap regionMap, NRGStackWithParams nrgStack) throws CheckException {
		
		try {
			MarkEllipsoid me = (MarkEllipsoid) mark;
			
			double minBound = getSharedObjects().getMarkBounds().getMinRslvd(nrgStack.getDimensions().getRes(), true);
			double maxBound = getSharedObjects().getMarkBounds().getMaxRslvd(nrgStack.getDimensions().getRes(), true);
			
			double[] normalisedRadii = EllipsoidUtilities.normalisedRadii( me, nrgStack.getDimensions().getRes() );
			
			for( int i=0; i<3; i++) {
				if (normalisedRadii[i] < minBound) {
					return false;
				}
				if (normalisedRadii[i] > maxBound) {
					return false;
				}		
			}
			
		} catch (GetOperationFailedException e) {
			throw new CheckException("Cannot establish bounds", e);
		}
		
		return true;
	}

	@Override
	public boolean isCompatibleWith(Mark testMark) {
		return testMark instanceof MarkEllipsoid;
	}

	@Override
	public FeatureList orderedListOfFeatures() {
		return null;
	}
}
