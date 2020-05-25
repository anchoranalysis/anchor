package org.anchoranalysis.image.chnl;

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


import java.nio.Buffer;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.chnl.factory.ChnlFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramFactory;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.voxel.box.BoundedVoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.VoxelBoxWrapper;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;


public class Chnl {

	private ImageDim dim;
	private BoundedVoxelBox<? extends Buffer> delegate;
	
	private static ChnlFactory factory = ChnlFactory.instance();
	
	// Constructor
	public Chnl( VoxelBox<? extends Buffer> bufferAccess, ImageRes res ) {
		this.dim = new ImageDim(bufferAccess.extnt(), res);
		delegate = new BoundedVoxelBox<>( new BoundingBox(bufferAccess.extnt()), bufferAccess);
	}
	
	public ObjMask equalMask( BoundingBox bbox, int equalVal ) {
		return delegate.getVoxelBox().equalMask(bbox, equalVal );
	}
	
	public VoxelBoxWrapper getVoxelBox() {
		return new VoxelBoxWrapper(delegate.getVoxelBox());
	}
	
	public void replaceVoxelBox( VoxelBoxWrapper vbNew ) {
		this.delegate.setVoxelBoxSkipType( vbNew.any() );
	}
	
	// Creates a new channel contain a duplication only of a particular slice
	public Chnl extractSlice(int z) {
		return ChnlFactory.instance().create( delegate.getVoxelBox().extractSlice(z), getDimensions().getRes() );
	}
	
	public Chnl scaleXY( double ratioX, double ratioY, Interpolator interpolator ) {
		// We round as sometimes we get values which, for example, are 7.999999, intended to be 8, due to how we use our ScaleFactors
		int newSizeX = (int) Math.round(ratioX * getDimensions().getX());
		int newSizeY = (int) Math.round(ratioY * getDimensions().getY());
		return resizeXY( newSizeX, newSizeY, interpolator ); 
	}
	
	public Chnl resizeXY( int x, int y, Interpolator interpolator ) {
		
		assert( factory!=null );
		
		ImageDim sdNew = getDimensions().scaleXYTo(x,y);
		
		VoxelBox<? extends Buffer> ba = delegate.getVoxelBox().resizeXY(x, y, interpolator);
		assert(ba.extnt().getX()==x);
		assert(ba.extnt().getY()==y);
		assert(ba.extnt().getVolumeXY()==ba.getPixelsForPlane(0).buffer().capacity());
		return factory.create( ba, sdNew.getRes() );
	}
	
	public Chnl maxIntensityProj() {
		assert( factory!=null );
		int prevZSize = delegate.getVoxelBox().extnt().getZ();
		return factory.create( delegate.getVoxelBox().maxIntensityProj(), getDimensions().getRes().duplicateFlattenZ(prevZSize) );
	}
	
	public Chnl meanIntensityProj() {
		int prevZSize = delegate.getVoxelBox().extnt().getZ();
		return factory.create( delegate.getVoxelBox().meanIntensityProj(), getDimensions().getRes().duplicateFlattenZ(prevZSize) );
	}


	// Duplicates the current channel
	public Chnl duplicate() {
		Chnl dup = factory.create( delegate.getVoxelBox().duplicate(), getDimensions().getRes() );
		assert( dup.delegate.getVoxelBox().extnt().equals( delegate.getVoxelBox().extnt() ));
		return dup;
	}

	// The number of non-zero pixels.
	// Mainly intended for debugging, as it's useful in the debugger view
	public int numNonZeroPixels() {
		return delegate.getVoxelBox().countGreaterThan(0);
	}
	
	// TRUE if there is at least a single voxel with a particular value
	public boolean hasEqualTo( int value ) {
		return delegate.getVoxelBox().hasEqualTo(value);
	}
	
	// Counts the number of voxels with a value equal to a particular value
	public int countEqualTo( int value ) {
		return delegate.getVoxelBox().countEqual(value);
	}
	
	public ImageDim getDimensions() {
		return dim;
	}


	public VoxelDataType getVoxelDataType() {
		return delegate.getVoxelBox().dataType();
	}
	
	@Override
	public String toString() {
		Histogram h;
		try {
			h = HistogramFactory.create(this);
		} catch (CreateException e) {
			return String.format("Error: %s",e);
		}
		return h.toString();
	}
	
	public boolean equalsDeep( Chnl other) {
		return dim.equals(other.dim) && delegate.equalsDeep( other.delegate );
	}
}
