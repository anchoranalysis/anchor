package org.anchoranalysis.anchor.mpp.feature.bean.nrg.elem;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.feature.cache.calculation.CachedCalculation;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CalculateDeriveSingleMemoFromPair extends CachedCalculation<FeatureInputSingleMemo,FeatureInputPairMemo> {

	private boolean first;
	
	/**
	 * Constructor
	 * 
	 * @param first Iff true, first object is used, otherwise the second
	 */
	public CalculateDeriveSingleMemoFromPair(boolean first) {
		super();
		this.first = first;
	}
	
	@Override
	protected FeatureInputSingleMemo execute(FeatureInputPairMemo params) throws ExecuteException {
		return new FeatureInputSingleMemo(
			first ? params.getObj1() : params.getObj2(),
			params.getNrgStack()
		);
	}

	@Override
	public boolean equals(Object obj) {
		 if(obj instanceof CalculateDeriveSingleMemoFromPair){
			 final CalculateDeriveSingleMemoFromPair other = (CalculateDeriveSingleMemoFromPair) obj;
		        return new EqualsBuilder()
		            .append(first, other.first)
		            .isEquals();
	    } else{
	        return false;
	    }
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(first)
			.toHashCode();
	}
}
