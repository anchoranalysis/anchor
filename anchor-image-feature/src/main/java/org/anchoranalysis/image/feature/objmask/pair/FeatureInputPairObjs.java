package org.anchoranalysis.image.feature.objmask.pair;

import java.util.Optional;

import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

/*-
 * #%L
 * anchor-image-feature
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



import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ops.ObjMaskMerger;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * A pair of objects (left, and right) and maybe a merged version of both
 * 
 * <p>Note that left and right simply identify two parts of the pair (tuple). It has no physical meaning
 * related to where the objects are located in the scene.</p>.
 * 
 * <p>If a merged version doesn't exist, it is created and cached on demand.</p>
 * 
 * @author Owen Feehan
 *
 */
public class FeatureInputPairObjs extends FeatureInputNRGStack {

	private ObjMask left;
	private ObjMask right;
	
	private Optional<ObjMask> merged = Optional.empty();
	
	public FeatureInputPairObjs(ObjMask left, ObjMask right) {
		this(left, right, Optional.empty() );
	}
	
	public FeatureInputPairObjs(ObjMask left, ObjMask right, Optional<NRGStackWithParams> nrgStack) {
		this(left, right, nrgStack, Optional.empty() );
	}
	
	public FeatureInputPairObjs(
		ObjMask left,
		ObjMask right,
		Optional<NRGStackWithParams> nrgStack,
		Optional<ObjMask> merged
	) {
		super(nrgStack);
		this.left = left;
		this.right = right;
		this.merged = merged;
	}
		
	protected FeatureInputPairObjs( FeatureInputPairObjs src ) {
		super( src.getNrgStackOptional()  );
		this.left = src.left;
		this.right = src.right;
		this.merged = src.merged;
	}

	public ObjMask getLeft() {
		return left;
	}

	public ObjMask getRight() {
		return right;
	}
	
	/**
	 * Returns a merged version of the two-objects available (or NULL if not available) 
	 * 
	 * @return the merged object-mask
	 */
	public ObjMask getMerged() {
		if (!merged.isPresent()) {
			merged = Optional.of(
				ObjMaskMerger.merge(left, right)
			);
		}
		return merged.get();
	}

	@Override
	public FeatureInputPairObjs createInverse() {
		return new FeatureInputPairObjs(
			right,
			left,
			getNrgStackOptional(),
			merged
		);
	}

	@Override
	public String toString() {
		return String.format(
			"%s vs %s",
			left.centerOfGravity(),
			right.centerOfGravity()
		);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		
		if (!(obj instanceof FeatureInputPairObjs)) { return false; }
		
		FeatureInputPairObjs objCast = (FeatureInputPairObjs) obj;
		
		if (!left.equals(objCast.left)) {
			return false;
		}
		
		if (!right.equals(objCast.right)) {
			return false;
		}
		
		if (!merged.equals(objCast.merged)) {
			return false;
		}
		
		return true;

	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper( super.hashCode() )
				.append(left)
				.append(right)
				.append(merged)
				.toHashCode();
	}
}
