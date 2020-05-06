package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.feature.nrg.NRGPair;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.PxlMarkMemo;

/*-
 * #%L
 * anchor-mpp-feature
 * %%
 * Copyright (C) 2010 - 2019 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.results.ResultsVector;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.nrg.NRGTotal;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;

public class AddCriteriaNRGElemPair implements AddCriteria<NRGPair> {

	private FeatureList<FeatureInputPairMemo> nrgElemPairList;
	
	// List of criteria for adding pairs
	private AddCriteriaPair pairAddCriteria;

	private FeatureList<FeatureInputPairMemo> featuresAddCriteria;
	
	public AddCriteriaNRGElemPair( FeatureList<FeatureInputPairMemo> nrgElemPairList, AddCriteriaPair pairAddCriteria) throws InitException {
		super();
		
		this.nrgElemPairList = nrgElemPairList;
		this.pairAddCriteria = pairAddCriteria;
		
		try {
			this.featuresAddCriteria = this.pairAddCriteria.orderedListOfFeatures();
		} catch (CreateException e) {
			throw new InitException(e);
		}
	}


	/**
	 * @throws CreateException 
	 * 
	 */
	@Override
	public FeatureList<FeatureInputPairMemo> orderedListOfFeatures() throws CreateException {

		FeatureList<FeatureInputPairMemo> out = new FeatureList<>();
		
		// Now we add all the features we need from the nrgElemPairList
		out.addAll( nrgElemPairList );
		
		// We put our features from the AddCriteriaPair first
		out.addAll( featuresAddCriteria );
				
		return out;
	}

	// Returns NULL if to reject an edge
	@Override
	public NRGPair generateEdge(
		PxlMarkMemo mark1,
		PxlMarkMemo mark2,
		NRGStackWithParams nrgStack,
		FeatureCalculatorMulti<FeatureInputPairMemo> session,
		boolean use3D
	) throws CreateException {
		
		
		// We have to split our FeatureSession in two seperate sessions:
		//       some features for the includeMarks
		//   and some features for nrgElemPairList
		
		// If any of the add criteria indicate an edge, then we calc the features
		//  This will also ensure the params collection is fully populated with
		//  necessary calculations from the addCriteria calculations to be used later
		boolean calc = false;
		try {
			if (pairAddCriteria.includeMarks(mark1, mark2, nrgStack.getDimensions(), session, use3D)) {
				calc = true;
			}
		} catch (IncludeMarksFailureException e) {
			throw new CreateException(e);
		}
	
		if (calc) {
			try {
				FeatureInputPairMemo params = new FeatureInputPairMemo(
					mark1,
					mark2,
					nrgStack
				);
				ResultsVector rv = session.calc(params, nrgElemPairList);

				Pair<Mark> pair = new Pair<>( mark1.getMark(), mark2.getMark() );
				return new NRGPair(pair, new NRGTotal(rv.total()) );
			} catch( FeatureCalcException e ) {
				throw new CreateException(e);
			}
		} else {
			return null;
		}
	}


	@Override
	public boolean paramsEquals(Object other) {

		if (!(other instanceof AddCriteriaNRGElemPair)) {
			return false;
		}
		
		AddCriteriaNRGElemPair obj = (AddCriteriaNRGElemPair) other;
		
		if (!nrgElemPairList.equals(obj.nrgElemPairList)) {
			return false;
		}
		
		if (!pairAddCriteria.paramsEquals(obj.pairAddCriteria)) {
			return false;
		}
		
		return true;
	}

	
}
