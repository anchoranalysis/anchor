package org.anchoranalysis.image.experiment.bean.seed;

/*
 * #%L
 * anchor-image-experiment
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
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.ExperimentExecutionException;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.experiment.seed.SeedFinderException;
import org.anchoranalysis.image.extent.ImageDim;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.seed.SeedCollection;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

/**
 * Applies the seed finding on a MIP version of the seeds
 * The returned seed has no Z dimension
 * 
 * @author Owen Feehan
 *
 * @param <S> shared-state
 */
public class SeedFinderMIP<S> extends SeedFinder<S> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private SeedFinder<S> seedFinder;
	// END BEAN PROPERTIES
	
	@Override
	public SeedCollection findSeeds(NamedImgStackCollection stackCollection, INamedProvider<ObjMaskCollection> objMaskProvider, ExperimentExecutionArguments expArgs, KeyValueParams keyValueParams, LogErrorReporter logger, BoundOutputManagerRouteErrors outputManager, S sharedState) throws SeedFinderException {
		

		try {
			ImageDim sd = stackCollection.getException(ImgStackIdentifiers.INPUT_IMAGE).getDimensions();
			
			NamedImgStackCollection stackCollectionMIP = stackCollection.maxIntensityProj();

			
			SeedCollection seeds = seedFinder.findSeeds(stackCollectionMIP, objMaskProvider, expArgs, keyValueParams, logger, outputManager, sharedState);
			seeds.growToZ( sd.getZ() );
			return seeds;
		} catch (OptionalOperationUnsupportedException | GetOperationFailedException e) {
			throw new SeedFinderException(e);
		}
	}

	public SeedFinder<S> getSeedFinder() {
		return seedFinder;
	}

	public void setSeedFinder(SeedFinder<S> seedFinder) {
		this.seedFinder = seedFinder;
	}

	@Override
	public S beforeAnySeedFinding(
			BoundOutputManagerRouteErrors outputManager)
			throws ExperimentExecutionException {
		return seedFinder.beforeAnySeedFinding(outputManager);
	}

	@Override
	public void afterAllSeedFinding(
			BoundOutputManagerRouteErrors outputManager, S sharedState)
			throws ExperimentExecutionException {
		seedFinder.afterAllSeedFinding(outputManager, sharedState);
	}

}
