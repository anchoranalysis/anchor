package org.anchoranalysis.image.init;

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
import java.util.Set;

import org.anchoranalysis.core.bridge.IObjectBridge;
import org.anchoranalysis.core.error.AnchorNeverOccursException;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.name.provider.NamedProviderBridge;
import org.anchoranalysis.core.name.provider.NamedProviderCombine;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.image.binary.BinaryChnl;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.chnl.factory.ChnlFactorySingleType;
import org.anchoranalysis.image.chnl.factory.ChnlFactoryByte;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.stack.Stack;

class CombineDiverseProvidersAsStacks implements INamedProvider<Stack> {

	private INamedProvider<Stack> stackCollection;
	private INamedProvider<Chnl> namedChnlCollection;
	private INamedProvider<BinaryChnl> namedBinaryImageCollection;
	private INamedProvider<Stack> combinedStackProvider;
	
	public CombineDiverseProvidersAsStacks(
		INamedProvider<Stack> stackCollection,
		INamedProvider<Chnl> namedChnlCollection,
		INamedProvider<BinaryChnl> namedBinaryImageCollection
	) {
		this.stackCollection = stackCollection;
		this.namedChnlCollection = namedChnlCollection;
		this.namedBinaryImageCollection = namedBinaryImageCollection;
		
		combinedStackProvider = createCombinedStackProvider();
	}

	private INamedProvider<Stack> createCombinedStackProvider() {
		
		// Channel collection bridge
		NamedProviderBridge<Chnl,Stack> namedChnlCollectionAsStackBridge =
			new NamedProviderBridge<>(
				namedChnlCollection,
				new ChnlToStackBridge(),
				false
			);

		// Binary Img Collection bridge
		NamedProviderBridge<BinaryChnl,Stack> namedBinaryChnlCollectionAsStackBridge =
			new NamedProviderBridge<>(
				namedBinaryImageCollection,
				new BinaryChnlToStackBridge(),
				false
			);
		
		NamedProviderCombine<Stack> combined = new NamedProviderCombine<>(); 
		combined.add( stackCollection );
		combined.add( namedChnlCollectionAsStackBridge );
		combined.add( namedBinaryChnlCollectionAsStackBridge );
		return combined;
	}

	public INamedProvider<Stack> getCombinedStackProvider() {
		return combinedStackProvider;
	}
	
	@Override
	public Stack getException(String key)
			throws NamedProviderGetException {
		return combinedStackProvider.getNull(key);
	}

	@Override
	public Stack getNull(String key)
			throws NamedProviderGetException {
		return combinedStackProvider.getException(key);
	}

	@Override
	public Set<String> keys() {
		return combinedStackProvider.keys();
	}

	// Note we expect that sourceObject is never NULL
	
	private static class ChnlToStackBridge implements IObjectBridge<Chnl,Stack,AnchorNeverOccursException> {
		
		@Override
		public Stack bridgeElement(Chnl sourceObject) {
			return new Stack( sourceObject ); 
		}
	}
	
	private static class BinaryChnlToStackBridge implements IObjectBridge<BinaryChnl,Stack,AnchorNeverOccursException> {
		
		private ChnlFactorySingleType factory = new ChnlFactoryByte();
		
		@Override
		public Stack bridgeElement(	BinaryChnl sourceObject ) {
			
			Chnl chnlNew = factory.createEmptyInitialised( sourceObject.getChnl().getDimensions() );
				
			BinaryVoxelBox<ByteBuffer> bvb = sourceObject.binaryVoxelBox();
			
			// For each region we get a mask for what equals the binary mask
			ObjMask om = bvb.getVoxelBox().equalMask(
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

}