package org.anchoranalysis.image.feature.object.input;

import java.util.Optional;

import org.anchoranalysis.feature.input.FeatureInputNRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.anchoranalysis.image.objectmask.ops.ObjectMaskMerger;
import org.apache.commons.lang.builder.HashCodeBuilder;


/**
 * A pair of objects (first and second) and maybe a merged version of both
 * 
 * <p>Note that left and right simply identify two parts of the pair (tuple). It has no physical meaning
 * related to where the objects are located in the scene.</p>.
 * 
 * <p>If a merged version doesn't exist, it is created and cached on demand.</p>
 * 
 * @author Owen Feehan
 *
 */
public class FeatureInputPairObjects extends FeatureInputNRGStack {

	private ObjectMask first;
	private ObjectMask second;
	
	private Optional<ObjectMask> merged = Optional.empty();
	
	public FeatureInputPairObjects(ObjectMask first, ObjectMask second) {
		this(first, second, Optional.empty() );
	}
	
	public FeatureInputPairObjects(ObjectMask first, ObjectMask second, Optional<NRGStackWithParams> nrgStack) {
		this(first, second, nrgStack, Optional.empty() );
	}
	
	public FeatureInputPairObjects(
		ObjectMask first,
		ObjectMask second,
		Optional<NRGStackWithParams> nrgStack,
		Optional<ObjectMask> merged
	) {
		super(nrgStack);
		this.first = first;
		this.second = second;
		this.merged = merged;
	}
		
	protected FeatureInputPairObjects( FeatureInputPairObjects src ) {
		super( src.getNrgStackOptional()  );
		this.first = src.first;
		this.second = src.second;
		this.merged = src.merged;
	}

	public ObjectMask getFirst() {
		return first;
	}

	public ObjectMask getSecond() {
		return second;
	}
	
	/**
	 * Returns a merged version of the two-objects available (or NULL if not available) 
	 * 
	 * @return the merged object-mask
	 */
	public ObjectMask getMerged() {
		if (!merged.isPresent()) {
			merged = Optional.of(
				ObjectMaskMerger.merge(first, second)
			);
		}
		return merged.get();
	}
	
	public Optional<ObjectMask> getMergedOptional() {
		return merged;
	}

	@Override
	public String toString() {
		return String.format(
			"%s vs %s",
			first.centerOfGravity(),
			second.centerOfGravity()
		);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (!super.equals(obj)) { return false; }
		
		if (!(obj instanceof FeatureInputPairObjects)) { return false; }
		
		FeatureInputPairObjects objCast = (FeatureInputPairObjects) obj;
		
		if (!first.equals(objCast.first)) {
			return false;
		}
		
		if (!second.equals(objCast.second)) {
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
				.append(first)
				.append(second)
				.append(merged)
				.toHashCode();
	}
}
