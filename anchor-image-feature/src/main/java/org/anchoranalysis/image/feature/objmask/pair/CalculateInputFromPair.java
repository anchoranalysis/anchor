package org.anchoranalysis.image.feature.objmask.pair;

/*-
 * #%L
 * anchor-plugin-image-feature
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.feature.cache.calculation.FeatureCalculation;
import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObj;
import org.anchoranalysis.image.objectmask.ObjectMask;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * Calculates a single-input from a pair
 * 
 * <div>
 * Three states are possible:
 * <ul>
 * <li>First</li>
 * <li>Second</li>
 * <li>Merged</li>
 * </div>
 * 
 * @author Owen Feehan
 *
 */
public class CalculateInputFromPair extends FeatureCalculation<FeatureInputSingleObj, FeatureInputPairObjs> {

	public enum Extract {
		FIRST,
		SECOND,
		MERGED
	}
	
	private Extract extract;
	
	/**
	 * Constructor
	 * 
	 * @param merged iff-true the merged object of the pair is used (and the parameter first is ignored)
	 * 	@param firstOrSecond iff-true the first object of the pair is used for creating params, otherwise the second object 
	 */
	public CalculateInputFromPair(Extract extract) {
		super();
		this.extract = extract;
	}

	@Override
	protected FeatureInputSingleObj execute(FeatureInputPairObjs input) {
		FeatureInputSingleObj paramsNew = new FeatureInputSingleObj(
			extractObj(input)
		);
		paramsNew.setNrgStack( input.getNrgStackOptional() );
		return paramsNew;
	}
	
	private ObjectMask extractObj(FeatureInputPairObjs input) {
		
		if (extract==Extract.MERGED) {
			return input.getMerged();
		}
		
		return extract==Extract.FIRST ? input.getFirst() : input.getSecond();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		CalculateInputFromPair rhs = (CalculateInputFromPair) obj;
		return new EqualsBuilder()
             .append(extract, rhs.extract)
             .isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(extract)
			.toHashCode();
	}

}
