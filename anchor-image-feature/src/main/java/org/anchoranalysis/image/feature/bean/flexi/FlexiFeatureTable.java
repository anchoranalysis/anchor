package org.anchoranalysis.image.feature.bean.flexi;

/*-
 * #%L
 * anchor-image-feature
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

import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.NamedBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.bean.list.FeatureListProvider;
import org.anchoranalysis.feature.calc.params.FeatureCalcParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.feature.flexi.FeatureSessionFlexiFeatureTable;
import org.anchoranalysis.image.objmask.ObjMaskCollection;

/**
 * A flexible feature-table.
 * 
 * A list of features is created dynamically according to certain rules.
 * 
 * List FeatureCalcParams are also created dynamically to be evaluated against those features.
 * 
 * @author Owen Feehan
 *
 */
public abstract class FlexiFeatureTable extends AnchorBean<FlexiFeatureTable> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates features that will be applied on the objMasks. Features should always be duplicated from the input list.
	 * 
	 * @param list
	 * @return
	 * @throws CreateException
	 */
	public abstract FeatureSessionFlexiFeatureTable createFeatures( List<NamedBean<FeatureListProvider>> list ) throws CreateException, InitException;
	
	public abstract List<FeatureCalcParams> createListCalcParams(
		ObjMaskCollection objs,
		NRGStackWithParams nrgStack,
		LogErrorReporter logErrorReporter
	) throws CreateException;
}
