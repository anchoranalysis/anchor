package org.anchoranalysis.annotation.io.assignment.generator;

/*-
 * #%L
 * anchor-annotation-io
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

import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.io.bean.color.generator.ColorSetGenerator;

public class ColorPool {

	private int numPaired;
	private ColorSetGenerator colorSetGeneratorPaired;
	private ColorSetGenerator colorSetGeneratorUnpaired;
	private boolean differentColorsForMatches;
	
	public ColorPool( int numPaired, ColorSetGenerator colorSetGeneratorPaired, ColorSetGenerator colorSetGeneratorUnpaired, boolean differentColorsForMatches ) {
		this.numPaired = numPaired;
		this.colorSetGeneratorPaired = colorSetGeneratorPaired;
		this.colorSetGeneratorUnpaired = colorSetGeneratorUnpaired;
		assert( colorSetGeneratorPaired != null);
		assert( colorSetGeneratorUnpaired != null);
		this.differentColorsForMatches = differentColorsForMatches;
	}
			
	public ColorList createColors( int numOtherObjs ) throws OperationFailedException {
		
		ColorList cols = new ColorList();
		
		if (differentColorsForMatches) {
			
			// Matched					
			cols.addAllScaled(
				colorSetGeneratorPaired.genColors( numPaired ),
				0.5
			);
			
			// Unmatched
			cols.addAll( colorSetGeneratorUnpaired.genColors(numOtherObjs) );
		} else {
			// Treat all as unmatched
			cols.addAll( colorSetGeneratorUnpaired.genColors(numPaired + numOtherObjs) );
		}

		return cols;
	}

	public boolean isDifferentColorsForMatches() {
		return differentColorsForMatches;
	}
}
