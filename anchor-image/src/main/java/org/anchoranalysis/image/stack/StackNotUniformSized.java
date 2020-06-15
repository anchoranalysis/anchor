package org.anchoranalysis.image.stack;

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

import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;



public class StackNotUniformSized implements Iterable<Chnl> {

	// We store our values in an arraylist of channels
	private ArrayList<Chnl> chnls;
	
	// Image stack
	public StackNotUniformSized() {
		chnls = new ArrayList<>();
	}
	
	// Create a stack from a channel
	public StackNotUniformSized( Chnl chnl ) {
		chnls = new ArrayList<>();
		addChnl( chnl );
	}
	
	public StackNotUniformSized extractSlice( int z ) {
		
		StackNotUniformSized stackOut = new StackNotUniformSized();
		for( int c=0; c<chnls.size(); c++) {
			stackOut.addChnl( chnls.get(c).extractSlice(z) );
		}
		return stackOut;
	}
	
	// TODO MAKE MORE EFFICIENT
	public StackNotUniformSized maxIntensityProj() {
		
		StackNotUniformSized stackOut = new StackNotUniformSized();
		for( int c=0; c<chnls.size(); c++) {
			// TODO make more efficient than duplicateByte()
			stackOut.addChnl( chnls.get(c).duplicate().maxIntensityProjection() );
		}
		return stackOut;
	}

	
	public void clear() {
		chnls.clear();
	}
	
	public final void addChnl( Chnl chnl ) {
		chnls.add( chnl );
	}
	
	public final Chnl getChnl( int index ) {
		return chnls.get(index);
	}
	
	public final int getNumChnl() {
		return chnls.size();
	}
	// 
	
	public ImageDim getFirstDimensions() {
		assert( getNumChnl() > 0 );
		return chnls.get(0).getDimensions();
	}
	
	public boolean isUniformSized() {

		if (chnls.size()<=1) {
			return true;
		}
		
		ImageDim sd = chnls.get(0).getDimensions();
		
		for (int c=1; c<chnls.size(); c++) {
			
			if (!sd.equals( chnls.get(c).getDimensions() )) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isUniformTyped() {

		if (chnls.size()<=1) {
			return true;
		}
		
		VoxelDataType dataType = chnls.get(0).getVoxelDataType();
		
		for (int c=1; c<chnls.size(); c++) {
			
			if (!dataType.equals( chnls.get(c).getVoxelDataType() )) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public Iterator<Chnl> iterator() {
		return chnls.iterator();
	}
	
	public StackNotUniformSized duplicate() {
		StackNotUniformSized out = new StackNotUniformSized();
		for( Chnl chnl : this) {
			out.addChnl( chnl.duplicate() );
		}
		return out;
	}
}
