package ch.ethz.biol.cell.imageprocessing.pixellist;

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

import ch.ethz.biol.cell.imageprocessing.pixellist.factory.PixelPartFactory;


public class IndexByChnl<PartType> {
	
	private ArrayList<IndexByRegion<PartType>> delegate = new ArrayList<>();

	public boolean add(IndexByRegion<PartType> e) {
		return delegate.add(e);
	}
	
	public void init( PixelPartFactory<PartType> factory, int numChnl, int numRegions, int numSlices ) {
		
		for (int i=0; i<numChnl; i++) {
			delegate.add( new IndexByRegion<>(factory,numRegions,numSlices) );
		}
	}

	public IndexByRegion<PartType> get(int index) {
		return delegate.get(index);
	}

	public int size() {
		return delegate.size();
	}
	
	public void cleanUp( PixelPartFactory<PartType> factory ) {
		for (int i=0; i<delegate.size(); i++) {
			delegate.get(i).cleanUp(factory);
		}
	}
}
