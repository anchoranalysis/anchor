package ch.ethz.biol.cell.mpp.nrg.nrgscheme.creator;

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
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Optional;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.bean.operator.Sum;

import ch.ethz.biol.cell.mpp.nrg.nrgscheme.NRGScheme;

public class NRGSchemeCreatorByElement extends NRGSchemeCreator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1630468482485141236L;
	
	// START BEAN PROPERTIES
	@BeanField
	private FeatureListProvider elemIndCreator;
	
	@BeanField
	private FeatureListProvider elemPairCreator;
	
	@BeanField @Optional
	private FeatureListProvider elemAllCreator;
	
	@BeanField
	private List<NamedBean<FeatureListProvider>> listImageFeatures = new ArrayList<NamedBean<FeatureListProvider>>();
	
	@BeanField
	private AddCriteriaPair pairAddCriteria;
	
	@BeanField
	private RegionMap regionMap;
	
	@BeanField @Optional
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
		
		out.setElemInd( elemIndCreator.create() );
		out.setElemPair( elemPairCreator.create() );
		out.setPairAddCriteria(pairAddCriteria);
		out.setRegionMap(regionMap);
	
		if (elemAllCreator!=null) {
			out.setElemAll( elemAllCreator.create() );
		}
		
		addImageFeatures( out.getListImageFeatures() );
				
		return out;
	}
	
	private void addImageFeatures( List<NamedBean<Feature>> imageFeatures ) throws CreateException {
		for( NamedBean<FeatureListProvider> ni : listImageFeatures) {
			FeatureList fl = ni.getValue().create();
			addImageFeature( fl, ni.getName(), imageFeatures );
		}
	}
	
	private void addImageFeature( FeatureList fl, String name, List<NamedBean<Feature>> imageFeatures ) {
		Sum feature = new Sum();
		feature.setList( fl );
		
		imageFeatures.add(
			new NamedBean<Feature>(
				nameForFeature( feature, name ),
				feature
			)
		);
	}
	
	private String nameForFeature( Feature feature, String name ) {
				
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

	public FeatureListProvider getElemIndCreator() {
		return elemIndCreator;
	}

	public void setElemIndCreator(FeatureListProvider elemIndCreator) {
		this.elemIndCreator = elemIndCreator;
	}

	public FeatureListProvider getElemPairCreator() {
		return elemPairCreator;
	}

	public void setElemPairCreator(FeatureListProvider elemPairCreator) {
		this.elemPairCreator = elemPairCreator;
	}

	public FeatureListProvider getElemAllCreator() {
		return elemAllCreator;
	}

	public void setElemAllCreator(FeatureListProvider elemAllCreator) {
		this.elemAllCreator = elemAllCreator;
	}

	public List<NamedBean<FeatureListProvider>> getListImageFeatures() {
		return listImageFeatures;
	}

	public void setListImageFeatures(
			List<NamedBean<FeatureListProvider>> listImageFeatures) {
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
