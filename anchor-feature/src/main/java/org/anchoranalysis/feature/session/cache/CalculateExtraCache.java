package org.anchoranalysis.feature.session.cache;

/*
 * #%L
 * anchor-feature
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


import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.feature.cachedcalculation.CachedCalculation;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class CalculateExtraCache extends CachedCalculation<FeatureSessionCacheRetriever> {

	// A unique identifier for the extra cache
	private String extraCacheUniqueIdentifier;
	
	private FeatureSessionCacheRetriever parentSession;
	
	private FeatureSessionCache subSession = null;
	
	public CalculateExtraCache(String extraCacheUniqueIdentifier, FeatureSessionCacheRetriever parentSession ) {
		super();
		this.extraCacheUniqueIdentifier = extraCacheUniqueIdentifier;
		this.parentSession = parentSession;
	}
	
//	@Override
//	protected FeatureSessionCacheRetriever execute(FeatureCalcParams params)
//			throws ExecuteException {
//		try {
//			return parentSession.createNewCache().retriever();
//		} catch (CreateException e) {
//			throw new ExecuteException(e);
//		}
//	}

	@Override
	public CachedCalculation<FeatureSessionCacheRetriever> duplicate() {
		return new CalculateExtraCache( extraCacheUniqueIdentifier, parentSession );
	}

	@Override
	public boolean equals(final Object obj){
	    if(obj instanceof CalculateExtraCache){
	        final CalculateExtraCache other = (CalculateExtraCache) obj;
	        return new EqualsBuilder()
	            .append(extraCacheUniqueIdentifier, other.extraCacheUniqueIdentifier)
	            .append(parentSession, other.parentSession)
	            .isEquals();
	    } else{
	        return false;
	    }
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(extraCacheUniqueIdentifier).append(parentSession).toHashCode();
	}

	@Override
	public void reset() {
		if (subSession!=null) {
			subSession.invalidate();
		}
	}

	@Override
	public FeatureSessionCacheRetriever getOrCalculate(FeatureCalcParams params)
			throws ExecuteException {
		if (subSession==null) {
			assert(parentSession.hasBeenInit());
			subSession = parentSession.createNewCache();
		}
		FeatureSessionCacheRetriever retriever = subSession.retriever();
		assert( retriever.hasBeenInit() );
		return retriever;
	}

	@Override
	public void assignResult(Object src) throws OperationFailedException {

		if (subSession!=null) {
			CalculateExtraCache otherCast = (CalculateExtraCache) src;
			subSession.assignResult( otherCast.subSession );
		}
		
	}

	@Override
	public boolean hasCachedCalculation() {
		return subSession!=null;
	}
}
