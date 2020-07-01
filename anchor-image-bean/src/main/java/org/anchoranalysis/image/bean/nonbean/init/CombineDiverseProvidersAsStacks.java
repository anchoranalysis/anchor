package org.anchoranalysis.image.bean.nonbean.init;

/*
 * #%L
 * anchor-mpp-sgmn
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


import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.Set;

import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderBridge;
import org.anchoranalysis.core.name.provider.NamedProviderCombine;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.Stack;

class CombineDiverseProvidersAsStacks implements NamedProvider<Stack> {

	private static ChannelFactorySingleType FACTORY = new ChannelFactoryByte();
	
	private NamedProvider<Stack> stackCollection;
	private NamedProvider<Channel> namedChnlCollection;
	private NamedProvider<BinaryChnl> namedBinaryImageCollection;
	private NamedProvider<Stack> combinedStackProvider;
	
	public CombineDiverseProvidersAsStacks(
		NamedProvider<Stack> stackCollection,
		NamedProvider<Channel> namedChnlCollection,
		NamedProvider<BinaryChnl> namedBinaryImageCollection
	) {
		this.stackCollection = stackCollection;
		this.namedChnlCollection = namedChnlCollection;
		this.namedBinaryImageCollection = namedBinaryImageCollection;
		
		combinedStackProvider = createCombinedStackProvider();
	}

	@Override
	public Optional<Stack> getOptional(String key) throws NamedProviderGetException {
		return combinedStackProvider.getOptional(key);
	}
	

	public NamedProvider<Stack> getCombinedStackProvider() {
		return combinedStackProvider;
	}

	@Override
	public Set<String> keys() {
		return combinedStackProvider.keys();
	}

	private NamedProvider<Stack> createCombinedStackProvider() {
		
		// Channel collection bridge
		NamedProviderBridge<Channel,Stack> namedChnlCollectionAsStackBridge =
			new NamedProviderBridge<>(
				namedChnlCollection,
				chnl -> new Stack(chnl),
				false
			);

		// Binary Img Collection bridge
		NamedProviderBridge<BinaryChnl,Stack> namedBinaryChnlCollectionAsStackBridge =
			new NamedProviderBridge<>(
				namedBinaryImageCollection,
				CombineDiverseProvidersAsStacks::stackFromBinary,
				false
			);
		
		NamedProviderCombine<Stack> combined = new NamedProviderCombine<>(); 
		combined.add( stackCollection );
		combined.add( namedChnlCollectionAsStackBridge );
		combined.add( namedBinaryChnlCollectionAsStackBridge );
		return combined;
	}

	private static Stack stackFromBinary( BinaryChnl sourceObject ) {
		
		Channel chnlNew = FACTORY.createEmptyInitialised( sourceObject.getChnl().getDimensions() );
		
		BinaryVoxelBox<ByteBuffer> bvb = sourceObject.binaryVoxelBox();
		
		// For each region we get a mask for what equals the binary mask
		ObjectMask om = bvb.getVoxelBox().equalMask(
			new BoundingBox(bvb.getVoxelBox().extent()),
			bvb.getBinaryValues().getOnInt()
		);
		try {
			chnlNew.getVoxelBox().asByte().replaceBy(om.getVoxelBox());
		} catch (IncorrectImageSizeException e) {
			assert false;
		}
		return new Stack( chnlNew ); 
	}
}