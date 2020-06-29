package org.anchoranalysis.anchor.mpp.feature.bean.nrgscheme;

/*
 * #%L
 * anchor-mpp
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
import java.util.List;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.bean.operator.Sum;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;

public class NRGSchemeCreatorByElement extends NRGSchemeCreator {

	// START BEAN PROPERTIES
	@BeanField
	private FeatureListProvider<FeatureInputSingleMemo> elemIndCreator;
	
	@BeanField
	private FeatureListProvider<FeatureInputPairMemo> elemPairCreator;
	
	@BeanField @OptionalBean
	private FeatureListProvider<FeatureInputAllMemo> elemAllCreator;
	
	@BeanField
	private List<NamedBean<FeatureListProvider<FeatureInputStack>>> listImageFeatures = new ArrayList<>();
	
	@BeanField
	private AddCriteriaPair pairAddCriteria;
	
	@BeanField
	private RegionMap regionMap;
	
	@BeanField @OptionalBean
	private KeyValueParamsProvider keyValueParamsProvider;
	
	/**
	 * If TRUE, the names of the imageFeatures are taken as a combination of the namedItem and the actual features
	 */
	@BeanField
	private boolean includeFeatureNames = false;
	// END BEAN PROPERTIES
	
	@Override
	public NRGScheme create() throws CreateException {
		
		NRGScheme out = new NRGScheme();
		
		if (keyValueParamsProvider!=null) {
			out.setKeyValueParamsProvider(keyValueParamsProvider);
		}
		
		out.setElemInd( elemIndCreator.create().asList() );
		out.setElemPair( elemPairCreator.create().asList() );
		out.setPairAddCriteria(pairAddCriteria);
		out.setRegionMap(regionMap);
	
		if (elemAllCreator!=null) {
			out.setElemAll( elemAllCreator.create().asList() );
		}
		
		addImageFeatures( out.getListImageFeatures() );
				
		return out;
	}
	
	private void addImageFeatures( List<NamedBean<Feature<FeatureInputStack>>> imageFeatures ) throws CreateException {
		for( NamedBean<FeatureListProvider<FeatureInputStack>> ni : listImageFeatures) {
			FeatureList<FeatureInputStack> fl = ni.getValue().create();
			addImageFeature( fl, ni.getName(), imageFeatures );
		}
	}
	
	private void addImageFeature( FeatureList<FeatureInputStack> fl, String name, List<NamedBean<Feature<FeatureInputStack>>> imageFeatures ) {
		Sum<FeatureInputStack> feature = new Sum<>();
		feature.setList( fl.asList() );
		
		imageFeatures.add(
			new NamedBean<Feature<FeatureInputStack>>(
				nameForFeature( feature, name ),
				feature
			)
		);
	}
	
	private String nameForFeature( Feature<?> feature, String name ) {
				
		if (includeFeatureNames) {
			return String.format("%s.%s", name, feature.getFriendlyName()  );
		} else {
			return name;
		}		
	}

	public AddCriteriaPair getPairAddCriteria() {
		return pairAddCriteria;
	}

	public void setPairAddCriteria(AddCriteriaPair pairAddCriteria) {
		this.pairAddCriteria = pairAddCriteria;
	}

	public FeatureListProvider<FeatureInputSingleMemo> getElemIndCreator() {
		return elemIndCreator;
	}

	public void setElemIndCreator(FeatureListProvider<FeatureInputSingleMemo> elemIndCreator) {
		this.elemIndCreator = elemIndCreator;
	}

	public FeatureListProvider<FeatureInputPairMemo> getElemPairCreator() {
		return elemPairCreator;
	}

	public void setElemPairCreator(FeatureListProvider<FeatureInputPairMemo> elemPairCreator) {
		this.elemPairCreator = elemPairCreator;
	}

	public FeatureListProvider<FeatureInputAllMemo> getElemAllCreator() {
		return elemAllCreator;
	}

	public void setElemAllCreator(FeatureListProvider<FeatureInputAllMemo> elemAllCreator) {
		this.elemAllCreator = elemAllCreator;
	}

	public List<NamedBean<FeatureListProvider<FeatureInputStack>>> getListImageFeatures() {
		return listImageFeatures;
	}

	public void setListImageFeatures(
			List<NamedBean<FeatureListProvider<FeatureInputStack>>> listImageFeatures) {
		this.listImageFeatures = listImageFeatures;
	}

	public RegionMap getRegionMap() {
		return regionMap;
	}

	public void setRegionMap(RegionMap regionMap) {
		this.regionMap = regionMap;
	}

	public boolean isIncludeFeatureNames() {
		return includeFeatureNames;
	}

	public void setIncludeFeatureNames(boolean includeFeatureNames) {
		this.includeFeatureNames = includeFeatureNames;
	}

	public KeyValueParamsProvider getKeyValueParamsProvider() {
		return keyValueParamsProvider;
	}

	public void setKeyValueParamsProvider(KeyValueParamsProvider keyValueParamsProvider) {
		this.keyValueParamsProvider = keyValueParamsProvider;
	}


}
