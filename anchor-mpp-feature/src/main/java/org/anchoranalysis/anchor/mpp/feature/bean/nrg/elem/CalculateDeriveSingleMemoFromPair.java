package org.anchoranalysis.anchor.mpp.feature.bean.nrg.elem;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CalculateDeriveSingleMemoFromPair extends FeatureCalculation<FeatureInputSingleMemo,FeatureInputPairMemo> {

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
	protected FeatureInputSingleMemo execute(FeatureInputPairMemo input) {
		return new FeatureInputSingleMemo(
			first ? input.getObj1() : input.getObj2(),
			input.getNrgStackOptional()
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
