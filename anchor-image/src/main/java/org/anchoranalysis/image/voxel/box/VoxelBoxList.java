package org.anchoranalysis.image.voxel.box;

/*
 * #%L
 * anchor-image
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
import java.util.Iterator;
import java.util.List;

import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;

public class VoxelBoxList implements Iterable<VoxelBoxWrapper>{

	private ArrayList<VoxelBoxWrapper> list = new ArrayList<>();

	public boolean add(VoxelBoxWrapper vb) {
		return list.add(vb);
	}

	@Override
	public Iterator<VoxelBoxWrapper> iterator() {
		return list.iterator();
	}

	public Extent getFirstExtent() {
		return list.get(0).any().extent();
	}

	public List<VoxelBuffer<?>> bufferListForSlice(int sliceNum) {
		List<VoxelBuffer<?>> listOut = new ArrayList<>(); 
		for (int i=0; i<list.size(); i++) {
			listOut.add( list.get(i).any().getPixelsForPlane(sliceNum) );
		}
		return listOut;
	}
	
	
	public VoxelBoxWrapper get(int index) {
		return list.get(index);
	}

	public int size() {
		return list.size();
	}
	
}
