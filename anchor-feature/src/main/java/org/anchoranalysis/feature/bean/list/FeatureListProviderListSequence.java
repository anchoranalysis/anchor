package org.anchoranalysis.feature.bean.list;

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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.bean.shared.SequenceInteger;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.operator.FeatureListElem;
import org.anchoranalysis.feature.bean.operator.Reference;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;

/**
 * Populates a FeatureListElem with references to other features formed by one
 *   or two sequences of integers, so that all the features of the following pattern
 *   are contained in the list.
 *   
 *   prependString seperator sequenceInteger
 *   prependString seperator sequenceInteger seperator sequenceAdditionalInteger
 *   
 *   where all these are concatenated together.
 * 
 * @author Owen Feehan
 *
 */
public class FeatureListProviderListSequence<T extends FeatureCalcParams> extends FeatureListProviderReferencedFeatures<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	/**
	 * The list feature that is duplicated, and populated.
	 */
	@BeanField @SkipInit
	private FeatureListElem<T> feature;
	
	@BeanField
	private String prependString;
	
	/**
	 * First number range that is appended
	 */
	@BeanField
	private SequenceInteger sequence;
	
	/**
	 * Another number range that is appended
	 */
	@BeanField @Optional
	private SequenceInteger sequenceAdditional;
	
	@BeanField
	private String seperator = ".";
	// END BEAN PROPERTIES
	
	private String determineFeatureName( String prepend, String append ) {
		StringBuilder sb = new StringBuilder();
		sb.append(prepend);
		sb.append(seperator);
		sb.append(append);
		return sb.toString();
	}
	
	private Feature<T> createReferenceFeature( String prepend, String append ) {
		String featureName = determineFeatureName(prepend, append);
		
		Reference<T> out = new Reference<>();
		out.setId(featureName);
		return out;
	}
	
	private Feature<T> createSingleSequence() {
		
		FeatureListElem<T> out = (FeatureListElem<T>) feature;
		
		for( int i=sequence.getStart(); i<=sequence.getEnd(); i+= sequence.getIncrement()) {
			out.getList().add( createReferenceFeature(prependString, Integer.toString(i)) );
		}
		
		return out;
	}
	
	private Feature<T> createDoubleSequence() {
		
		FeatureListElem<T> out = (FeatureListElem<T>) feature;
		
		for( int i=sequence.getStart(); i<=sequence.getEnd(); i+= sequence.getIncrement()) {
			for( int j=sequenceAdditional.getStart(); j<=sequenceAdditional.getEnd(); j+= sequenceAdditional.getIncrement()) {
				String suffix = String.format("%d%s%d",i,seperator,j);
				out.getList().add( createReferenceFeature(prependString, suffix ) );
			}
		}
		
		return out;
	}
	
	
	
	@Override
	public FeatureList<T> create() throws CreateException {
		
		FeatureList<T> out = new FeatureList<>();
		
		if (sequenceAdditional!=null) {
			out.add(createDoubleSequence());
		} else {
			out.add(createSingleSequence());
		}
				
		return out;
	}

	public String getPrependString() {
		return prependString;
	}

	public void setPrependString(String prependString) {
		this.prependString = prependString;
	}

	public SequenceInteger getSequence() {
		return sequence;
	}

	public void setSequence(SequenceInteger sequence) {
		this.sequence = sequence;
	}

	public SequenceInteger getSequenceAdditional() {
		return sequenceAdditional;
	}

	public void setSequenceAdditional(SequenceInteger sequenceAdditional) {
		this.sequenceAdditional = sequenceAdditional;
	}

	public String getSeperator() {
		return seperator;
	}

	public void setSeperator(String seperator) {
		this.seperator = seperator;
	}

	public FeatureListElem<T> getFeature() {
		return feature;
	}

	public void setFeature(FeatureListElem<T> feature) {
		this.feature = feature;
	}

}
