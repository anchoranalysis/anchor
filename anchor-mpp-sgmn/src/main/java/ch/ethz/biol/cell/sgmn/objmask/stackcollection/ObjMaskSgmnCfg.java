package ch.ethz.biol.cell.sgmn.objmask.stackcollection;

import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;

/*
 * #%L
 * anchor-mpp-sgmn
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
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.experiment.bean.sgmn.SgmnObjMaskCollection;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.seed.SeedCollection;
import org.anchoranalysis.image.sgmn.SgmnFailedException;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

import ch.ethz.biol.cell.mpp.cfg.Cfg;
import ch.ethz.biol.cell.sgmn.cfg.CfgSgmn;

public class ObjMaskSgmnCfg extends SgmnObjMaskCollection {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField
	private CfgSgmn cfgSgmn;
	// END BEAN PROPERTIES

	@Override
	public ObjMaskCollection sgmn(NamedImgStackCollection stackCollection,
			INamedProvider<ObjMaskCollection> objMaskCollection, SeedCollection seeds, RandomNumberGenerator re, ExperimentExecutionArguments expArgs, LogErrorReporter logger, BoundOutputManagerRouteErrors outputManager)
			throws SgmnFailedException {

		try {
			ImageDim sd = stackCollection.getException(ImgStackIdentifiers.INPUT_IMAGE).getDimensions();
			
			Cfg cfg = cfgSgmn.sgmn(stackCollection, objMaskCollection, expArgs, new KeyValueParams(), logger, outputManager );
			return cfg.calcMask(
				sd,
				RegionMapSingleton.instance().membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE),
				BinaryValuesByte.getDefault(),
				null
			).collectionObjMask();
			
		} catch (GetOperationFailedException e) {
			throw new SgmnFailedException(e);
		}
	}

	public CfgSgmn getCfgSgmn() {
		return cfgSgmn;
	}

	public void setCfgSgmn(CfgSgmn cfgSgmn) {
		this.cfgSgmn = cfgSgmn;
	}
}
