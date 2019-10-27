package ch.ethz.biol.cell.mpp.probmap;

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


import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.core.random.RandomNumberGenerator;

// A set of weights for a number of items
public class ProbWeights {

	private List<Double> listWeights = new ArrayList<>();
	private List<Double> listWeightsCum = new ArrayList<>();

	public void add(Double e) {
		
		listWeights.add(e);
		
		// Update cumulative
		if (listWeightsCum.size()==0) {
			// First
			listWeightsCum.add( e );
		} else {
			listWeightsCum.add( listWeightsCum.get(listWeightsCum.size()-1) + e );
			// Others
		}
	}
	
	public double getTotal() {
		if (listWeightsCum.size()==0) {
			return 0;
		}
		return listWeightsCum.get( listWeightsCum.size()-1 );
	}
	
	public int sample(RandomNumberGenerator re) {
		
		double tot = getTotal();
		
		// Our returned value
		double val = re.nextDouble() * tot;
		
		// TODO REPLACE WITH SOMETHING MORE EFFICIENT than looping through the cumulative values
		//   e.g. some kind of balanced binary search tree
		
		// We iterate forwardly until we find the correct interval
		for( int i=0; i<listWeightsCum.size(); i++) {
			if (listWeightsCum.get(i)>=val) {
				return i;
			}
		}
		// we shouldn't be able to get here, apart from some perhaps floating point round-up error
		return listWeightsCum.size()-1;
	}

	public int size() {
		return listWeights.size();
	}
	
}
