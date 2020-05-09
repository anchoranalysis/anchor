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
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.FeatureRelatedBean;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.input.FeatureInput;
import org.anchoranalysis.image.feature.stack.FeatureInputStack;

/**
 * An NRG Scheme sums lists of features, that can be divided into different cliques:
 * 
 * elemInd:  individual terms of clique-size==1     f_1(x)
 * elemPair:  pairwise terms of clique-size==2		f_2(x,y)
 * elemAll:  terms that include every item in the set   f_all(x_1,x_2,....x_n) for all n 
 * 
 * @author Owen Feehan
 *
 */
public class NRGScheme extends FeatureRelatedBean<NRGScheme> {

	// START BEAN PROPERTIES
	@BeanField @OptionalBean
	private FeatureList<FeatureInputPairMemo> elemPair = null;
	
	@BeanField @OptionalBean
	private FeatureList<FeatureInputSingleMemo> elemInd = null;
	
	@BeanField @OptionalBean
	private FeatureList<FeatureInputAllMemo> elemAll = null;
	
	@BeanField
	private RegionMap regionMap;
	
	/** A list of features of the image that are calculated first, and exposed to the other features as parameters */
	@BeanField
	private List<NamedBean<Feature<FeatureInputStack>>> listImageFeatures;
	
	@BeanField
	private AddCriteriaPair pairAddCriteria = null;
	
	@BeanField @OptionalBean
	private KeyValueParamsProvider keyValueParamsProvider;
	// END BEAN PROPERTIES
	
	public NRGScheme() {
		elemPair = new FeatureList<>();
		elemInd = new FeatureList<>();
		elemAll = new FeatureList<>();
		listImageFeatures = new ArrayList<NamedBean<Feature<FeatureInputStack>>>();
		regionMap = RegionMapSingleton.instance(); 
	}
	
	public NRGScheme( FeatureList<FeatureInputSingleMemo> elemInd, FeatureList<FeatureInputPairMemo> elemPair, RegionMap regionMap ) {
		this.elemPair = elemPair;
		this.elemInd = elemInd;
		this.elemAll = new FeatureList<>();
		this.regionMap = regionMap;
	}
	
	public void add( NRGScheme src ) {
		elemPair.addAll( src.elemPair );
		elemInd.addAll( src.elemInd );
		elemAll.addAll( src.elemAll );
		listImageFeatures.addAll( src.listImageFeatures );
	}

	/*** returns the associated KeyValueParams or an empty set, if no params are associated with the nrgScheme */
	public KeyValueParams createKeyValueParams() throws CreateException {
		if (keyValueParamsProvider!=null) {
			return keyValueParamsProvider.create().duplicate();
		} else {
			return new KeyValueParams();
		}
	}
	
	//! Checks that a mark's initial parameters are correct
	@Override
	public void checkMisconfigured( BeanInstanceMap defaultInstances ) throws BeanMisconfiguredException {
		
		super.checkMisconfigured( defaultInstances );
		
		if (elemInd==null && elemPair==null && elemAll==null ) {
			throw new BeanMisconfiguredException("At least one of elemInd and elemPair and elemAll must be non-null");
		}
		
		if (elemPair==null) {
			elemPair = new FeatureList<>();
		}
		
		if (elemInd==null) {
			elemInd = new FeatureList<>();
		}
		
		if (elemAll==null) {
			elemAll = new FeatureList<>();
		}
		
		if ((elemInd.size()+elemPair.size()+elemAll.size())==0) {
			throw new BeanMisconfiguredException("At least one NRG element must be specified");
		}
	}

	
	public List<Feature<FeatureInputPairMemo>> getElemPair() {
		return elemPair;
	}

	public void setElemPair(List<Feature<FeatureInputPairMemo>> elemPair) {
		this.elemPair = new FeatureList<>(elemPair);
	}

	// -1 means everything
	
	/**
	 * 
	 * @param cliqueSize 1 for pairwise, 0 for unary, -1 for all
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends FeatureInput> FeatureList<T> getElemByCliqueSize( int cliqueSize ) {
		
		if (cliqueSize==0) {
			return (FeatureList<T>) getElemIndAsFeatureList();
		} else if (cliqueSize==1) {
			return (FeatureList<T>) getElemPairAsFeatureList();
		} else if (cliqueSize==-1) {
			return (FeatureList<T>) getElemAllAsFeatureList();
		} else {
			assert false;
			return null;
		}
	}
	
	public FeatureList<FeatureInputSingleMemo> getElemIndAsFeatureList() {
		return elemInd;
	}
	
	public FeatureList<FeatureInputPairMemo> getElemPairAsFeatureList() {
		return elemPair;
	}
	
	public FeatureList<FeatureInputAllMemo> getElemAllAsFeatureList() {
		return elemAll;
	}	
	
	public List<Feature<FeatureInputSingleMemo>> getElemInd() {
		return elemInd;
	}
	


	public List<Feature<FeatureInputAllMemo>> getElemAll() {
		return elemAll;
	}
	
	public void setElemAll(List<Feature<FeatureInputAllMemo>> elemAll) {
		this.elemAll = new FeatureList<>(elemAll);
	}
	


	// We put our normal setters and getters as List<Feature> so that the bean xml can use the normal factories for Lists
	public void setElemInd(List<Feature<FeatureInputSingleMemo>> elemInd) {
		this.elemInd = new FeatureList<>(elemInd);
	}

	public AddCriteriaPair getPairAddCriteria() {
		return pairAddCriteria;
	}

	public void setPairAddCriteria(AddCriteriaPair pairAddCriteria) {
		this.pairAddCriteria = pairAddCriteria;
	}
	
	
	public NRGScheme duplicateKeepFeatures() {
		NRGScheme out = new NRGScheme();
		out.add(this);
		return out;
	}

	public List<NamedBean<Feature<FeatureInputStack>>> getListImageFeatures() {
		return listImageFeatures;
	}

	public void setListImageFeatures(
			List<NamedBean<Feature<FeatureInputStack>>> listImageFeatures) {
		this.listImageFeatures = listImageFeatures;
	}

	public RegionMap getRegionMap() {
		return regionMap;
	}

	public void setRegionMap(RegionMap regionMap) {
		this.regionMap = regionMap;
	}

	public KeyValueParamsProvider getKeyValueParamsProvider() {
		return keyValueParamsProvider;
	}

	public void setKeyValueParamsProvider(KeyValueParamsProvider keyValueParamsProvider) {
		this.keyValueParamsProvider = keyValueParamsProvider;
	}

}
