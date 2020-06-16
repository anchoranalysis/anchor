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
import java.util.List;

import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.extent.ImageRes;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * One ore more single-channel images that all have the same dimensions.
 * 
 * <p>This is one of the fundamental image data structures in Anchor.</p>
 * 
 * @author Owen Feehan
 *
 */
public class Stack implements Iterable<Channel> {

	private final StackNotUniformSized delegate;
	
	public Stack() {
		delegate = new StackNotUniformSized();
	}
	
	public Stack( ImageDim sd, ChannelFactorySingleType factory, int numChnls ) {
		delegate = new StackNotUniformSized();
		for( int i=0; i<numChnls; i++) {
			delegate.addChnl( factory.createEmptyInitialised(sd) );
		}
	}
	
	public Stack( Channel chnl ) {
		delegate = new StackNotUniformSized( chnl );
	}
	
	public Stack( Channel chnl0, Channel chnl1,  Channel chnl2 ) throws IncorrectImageSizeException {
		super();
		delegate = new StackNotUniformSized();
		addChnl(chnl0);
		addChnl(chnl1);
		addChnl(chnl2);
	}
	
	private Stack(StackNotUniformSized stack) {
		delegate = stack;
	}
		
	/** Copy constructor */
	private Stack(Stack src) {
		delegate = src.delegate.duplicate();
	}
	
	/** Produces a new stack with a particular operation applied to each channel.
	 * 
	 *  <p>The function applied to the channel should ensure it produces uniform sizes</p>
	 * 
	 * @param mapFunc performs an operation on a channel and produces a modified channel (or a different one entirely)
	 * @return a new stack containing mapFunc(chnl) preserving the channel order
	 * @throws IncorrectImageSizeException if the channels produced have non-uniform sizes
	 * */ 
	public Stack mapChnl( ChannelMapOperation mapFunc ) throws OperationFailedException {
		Stack out = new Stack();
		for( Channel c : this ) {
			try {
				out.addChnl(
					mapFunc.apply(c)
				);
			} catch (IncorrectImageSizeException e) {
				throw new OperationFailedException(e);
			}
		}
		return out;
	}
	
	public Stack extractSlice(int z) {
		// We know the sizes will be correct
		return new Stack(
			delegate.extractSlice(z)
		);
	}

	public Stack maxIntensityProj() {
		// We know the sizes will be correct
		return new Stack(
			delegate.maxIntensityProj()
		);
	}
	
	public void addBlankChnl()
			throws OperationFailedException {
			
		if (getNumChnl()==0) {
			throw new OperationFailedException("At least one channel must exist from which to guess dimensions");
		}
		
		if (!delegate.isUniformSized()) {
			throw new OperationFailedException("Other channels do not have the same dimensions. Cannot make a good guess of dimensions.");
		}
		
		if (!delegate.isUniformTyped()) {
			throw new OperationFailedException("Other channels do not have the same type.");
		}
		
		Channel first = getChnl(0);
		delegate.addChnl(
			ChannelFactory.instance().createEmptyInitialised(
				first.getDimensions(),
				first.getVoxelDataType()
			)
		);
		
	}

	public final void addChnl(Channel chnl) throws IncorrectImageSizeException {
		
		// We ensure that this channel has the same size as the first
		if (delegate.getNumChnl()>=1 && !chnl.getDimensions().equals(delegate.getChnl(0).getDimensions())) {
			throw new IncorrectImageSizeException("Dimensions of channel do not match existing channel");
		}
		
		delegate.addChnl(chnl);
	}

	public final Channel getChnl(int index) {
		return delegate.getChnl(index);
	}

	public final int getNumChnl() {
		return delegate.getNumChnl();
	}

	public ImageDim getDimensions() {
		return delegate.getChnl(0).getDimensions();
	}
	
	public Stack duplicate() {
		Stack out = new Stack(this);
		return out;
	}
	
	public Stack extractUpToThreeChnls() {
		Stack out = new Stack();
		int maxNum = Math.min(3, delegate.getNumChnl());
		for (int i=0; i<maxNum; i++) {
			try {
				out.addChnl( delegate.getChnl(i) );
			} catch (IncorrectImageSizeException e) {
				assert false;
			}
		}
		return out;
	}

	@Override
	public Iterator<Channel> iterator() {
		return delegate.iterator();
	}
	
	public List<Channel> asListChnls() {
		ArrayList<Channel> list = new ArrayList<>();
		for(int i=0; i<delegate.getNumChnl(); i++) {
			list.add( delegate.getChnl(i) );
		}
		return list;
	}
	
	// Returns true if the data type of all channels is equal to
	public boolean allChnlsHaveType( VoxelDataType chnlDataType ) {
		
		for (Channel chnl : this) {
			if (!chnl.getVoxelDataType().equals(chnlDataType)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) { return false; }
		if (obj == this) { return true; }
		
		if (!(obj instanceof Stack)) {
			return false;
		}
		
		Stack objCast = (Stack) obj;
					
		if (getNumChnl()!=objCast.getNumChnl()) {
			return false;
		}
		
		for( int i=0; i<getNumChnl(); i++) {
			if (!getChnl(i).equalsDeep(objCast.getChnl(i))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {

		HashCodeBuilder builder = new HashCodeBuilder()
				.append(getNumChnl());
		
		for( int i=0; i<getNumChnl(); i++) {
			builder.append( getChnl(i) );
		}
		
		return builder.toHashCode();
	}
	
	public void updateResolution(ImageRes res) {
		for( int i=0; i<getNumChnl(); i++) {
			getChnl(i).updateResolution(res);
		}
	}
	
}
