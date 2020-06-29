package org.anchoranalysis.anchor.mpp.feature.nrg.scheme;

import java.util.ArrayList;

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


import java.util.List;
import java.util.Optional;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.feature.addcriteria.AddCriteriaPair;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputAllMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputSingleMemo;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.Feature;
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
public class NRGScheme {

	private final FeatureList<FeatureInputSingleMemo> elemInd;
	private final FeatureList<FeatureInputPairMemo> elemPair;
	private final FeatureList<FeatureInputAllMemo> elemAll;
	
	private final RegionMap regionMap;
	
	/** A list of features of the image that are calculated first, and exposed to the other features as parameters */
	private final List<NamedBean<Feature<FeatureInputStack>>> listImageFeatures;
	
	private final AddCriteriaPair pairAddCriteria;
	
	private final Optional<KeyValueParamsProvider> keyValueParamsProvider;
	
	public NRGScheme(
		FeatureList<FeatureInputSingleMemo> elemInd,
		FeatureList<FeatureInputPairMemo> elemPair,
		FeatureList<FeatureInputAllMemo> elemAll,
		RegionMap regionMap,
		AddCriteriaPair pairAddCriteria
	) throws CreateException {
		this(
			elemInd,
			elemPair,
			elemAll,
			regionMap,
			pairAddCriteria,
			Optional.empty(),
			new ArrayList<>()
		);
	}
	
	public NRGScheme(
		FeatureList<FeatureInputSingleMemo> elemInd,
		FeatureList<FeatureInputPairMemo> elemPair,
		FeatureList<FeatureInputAllMemo> elemAll,
		RegionMap regionMap,
		AddCriteriaPair pairAddCriteria,
		Optional<KeyValueParamsProvider> keyValueParamsProvider,
		List<NamedBean<Feature<FeatureInputStack>>> listImageFeatures
	) throws CreateException {
		this.elemInd = elemInd;
		this.elemPair = elemPair;
		this.elemAll = elemAll;
		this.regionMap = regionMap;
		this.pairAddCriteria = pairAddCriteria;
		this.keyValueParamsProvider = keyValueParamsProvider;
		this.listImageFeatures = listImageFeatures;
		checkAtLeastOneNRGElement();
	}

	/*** returns the associated KeyValueParams or an empty set, if no params are associated with the nrgScheme */
	public KeyValueParams createKeyValueParams() throws CreateException {
		if (keyValueParamsProvider.isPresent()) {
			return keyValueParamsProvider.get().create().duplicate();
		} else {
			return new KeyValueParams();
		}
	}
	
	//! Checks that a mark's initial parameters are correct
	private void checkAtLeastOneNRGElement() throws CreateException {
		if ((elemInd.size()+elemPair.size()+elemAll.size())==0) {
			throw new CreateException("At least one NRG element must be specified");
		}
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

	public AddCriteriaPair getPairAddCriteria() {
		return pairAddCriteria;
	}

	public List<NamedBean<Feature<FeatureInputStack>>> getListImageFeatures() {
		return listImageFeatures;
	}

	public RegionMap getRegionMap() {
		return regionMap;
	}
}