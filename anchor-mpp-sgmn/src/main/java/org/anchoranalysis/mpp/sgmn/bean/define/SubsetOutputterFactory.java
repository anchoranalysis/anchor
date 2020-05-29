package org.anchoranalysis.mpp.sgmn.bean.define;

import java.util.Optional;

/*-
 * #%L
 * anchor-mpp-sgmn
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

import java.util.function.Function;

import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.io.objs.ObjMaskCollectionWriter;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.histogram.HistogramCSVGenerator;
import org.anchoranalysis.io.generator.serialized.XStreamGenerator;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;
import org.anchoranalysis.mpp.io.output.StackOutputKeys;


/**
 * This class will expect for the following second-level output keys:
 * cfg
 * hist
 * objs
 * 
 * @author Owen Feehan
 *
 */
class SubsetOutputterFactory {
	
	private MPPInitParams soMPP;
	private BoundOutputManagerRouteErrors outputManager;
	private boolean suppressSubfolders;
	
	public SubsetOutputterFactory(MPPInitParams soMPP, BoundOutputManagerRouteErrors outputManager,
			boolean suppressSubfolders) {
		super();
		this.soMPP = soMPP;
		this.outputManager = outputManager;
		this.suppressSubfolders = suppressSubfolders;
	}
	
	public SubsetOutputter<Cfg> cfg() {
		return create(
			soMPP.getCfgCollection(),
			new XStreamGenerator<Cfg>(
				Optional.of("cfg")
			),
			(BoundOutputManagerRouteErrors bom) -> bom.outputAllowedSecondLevel(StackOutputKeys.CFG),
			"cfgCollection"
		);
	}

	public SubsetOutputter<Histogram> histogram() {
		return create(
			soMPP.getImage().getHistogramCollection(),
			new HistogramCSVGenerator(),
			(BoundOutputManagerRouteErrors bom) -> bom.outputAllowedSecondLevel(StackOutputKeys.HISTOGRAM),
			"histogramCollection"
		);
	}
	
	public SubsetOutputter<ObjMaskCollection> objMask() {
		return create(
			soMPP.getImage().getObjMaskCollection(),
			ObjMaskCollectionWriter.generator(),
			(BoundOutputManagerRouteErrors bom) -> bom.outputAllowedSecondLevel(StackOutputKeys.OBJS),
			"objMaskCollection"
		);
	}
	
	private <T> SubsetOutputter<T> create(
			NamedProviderStore<T> store,
			IterableGenerator<T> generator,
			Function<BoundOutputManagerRouteErrors,OutputAllowed> outputAllowedFunc,
			String id
		) {
		return new SubsetOutputter<T>(
			store,
			outputAllowedFunc.apply(outputManager),
			generator,
			outputManager.getDelegate(),
			id,
			"",
			suppressSubfolders
		);
	}
}
