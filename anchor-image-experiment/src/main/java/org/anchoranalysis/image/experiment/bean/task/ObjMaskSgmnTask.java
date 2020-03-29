package org.anchoranalysis.image.experiment.bean.task;

import java.nio.file.Path;

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
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.bean.shared.random.RandomNumberGeneratorBean;
import org.anchoranalysis.bean.shared.random.RandomNumberGeneratorMersenneConstantBean;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.experiment.bean.sgmn.SgmnObjMaskCollection;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.input.NamedChnlsInput;
import org.anchoranalysis.image.io.input.series.NamedChnlCollectionForSeries;
import org.anchoranalysis.image.io.stack.StackCollectionOutputter;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.sgmn.SgmnFailedException;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

public class ObjMaskSgmnTask extends RasterTask {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8271422044212019000L;

	// START BEAN PROPERTIES
	@BeanField
	private SgmnObjMaskCollection sgmn = null;
	
	@BeanField
	private String outputNameOriginal = "original";
	
	@BeanField
	private RandomNumberGeneratorBean randomNumberGenerator = new RandomNumberGeneratorMersenneConstantBean();
	// END BEAN PROPERTIES
	
	public ObjMaskSgmnTask() {
		super();
	}
	
	@Override
	public boolean hasVeryQuickPerInputExecution() {
		return false;
	}

	@Override
	public void startSeries(BoundOutputManagerRouteErrors outputManager, ErrorReporter errorReporter) throws JobExecutionException {
		
	}
	
	
	@Override
	public void doStack( NamedChnlsInput inputObject, int seriesIndex, int numSeries, BoundOutputManagerRouteErrors outputManager, LogErrorReporter logErrorReporter, ExperimentExecutionArguments expArgs ) throws JobExecutionException {
		
		try {
			NamedImgStackCollection stackCollection = stacksForInput(
				inputObject,
				expArgs.getModelDirectory()
			);
			
			Chnl chnl = backgroundFromStacks(stackCollection);
			
			Outputter.writeOriginal(outputManager, chnl, outputNameOriginal);
			
			ObjMaskCollection objs = sgmnFromStacks(stackCollection, outputManager, logErrorReporter, expArgs);
			
			// Write different visualizations of the result
			Outputter.writeMaskOutputs(objs, chnl, outputManager);
			
		} catch (BeanDuplicateException | OperationFailedException e) {
			throw new JobExecutionException(e);
		} catch (NamedProviderGetException e) {
			throw new JobExecutionException(e.summarize());
		}
	}
	
	private ObjMaskCollection sgmnFromStacks(
		NamedImgStackCollection stackCollection,
		BoundOutputManagerRouteErrors outputManager,
		LogErrorReporter logErrorReporter,
		ExperimentExecutionArguments expArgs
	) throws OperationFailedException {
		
		SgmnObjMaskCollection sgmnDup = sgmn.duplicateBean();
		assert( sgmnDup != null );
		try {
			sgmnDup.initRecursive(logErrorReporter);
		} catch (InitException e) {
			throw new OperationFailedException(e);
		}
		
		try {
			return sgmnDup.sgmn(
				stackCollection,
				new LazyEvaluationStore<>(logErrorReporter, "objMaskCollection"),
				null,
				randomNumberGenerator.create(),
				expArgs,
				logErrorReporter,
				outputManager
			);
		} catch (SgmnFailedException e) {
			throw new OperationFailedException(e);
		}
	}
	
	private static NamedImgStackCollection stacksForInput( NamedChnlsInput inputObject, Path modelDir ) throws OperationFailedException {
		
		NamedImgStackCollection stackCollection = new NamedImgStackCollection();
		
		ProgressReporter progressReporter = ProgressReporterNull.get();
		
		NamedChnlCollectionForSeries ncc;
		try {
			ncc = inputObject.createChnlCollectionForSeries(0, progressReporter );
		} catch (RasterIOException e) {
			throw new OperationFailedException(e);
		}

		StackCollectionOutputter.copyFrom(
			ncc,
			stackCollection,
			modelDir,
			progressReporter
		);
		
		return stackCollection;
	}
	
	private static Chnl backgroundFromStacks( NamedImgStackCollection stackCollection ) throws NamedProviderGetException {
		return stackCollection.getException(ImgStackIdentifiers.INPUT_IMAGE).getChnl(0);
	}
	
	@Override
	public void endSeries(BoundOutputManagerRouteErrors outputManager) throws JobExecutionException {
		
	}

	public SgmnObjMaskCollection getSgmn() {
		return sgmn;
	}

	public void setSgmn(SgmnObjMaskCollection sgmn) {
		this.sgmn = sgmn;
	}

	public RandomNumberGeneratorBean getRandomNumberGenerator() {
		return randomNumberGenerator;
	}

	public void setRandomNumberGenerator(RandomNumberGeneratorBean randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}
}
