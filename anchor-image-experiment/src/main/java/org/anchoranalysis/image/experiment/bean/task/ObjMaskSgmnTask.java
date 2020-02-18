package org.anchoranalysis.image.experiment.bean.task;

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
import org.anchoranalysis.core.cache.ExecuteException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.name.store.LazyEvaluationStore;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.experiment.ExperimentExecutionArguments;
import org.anchoranalysis.experiment.JobExecutionException;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.experiment.bean.sgmn.SgmnObjMaskCollection;
import org.anchoranalysis.image.experiment.identifiers.ImgStackIdentifiers;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.generator.raster.ChnlGenerator;
import org.anchoranalysis.image.io.generator.raster.objmask.ObjMaskChnlGenerator;
import org.anchoranalysis.image.io.generator.raster.objmask.ObjMaskGenerator;
import org.anchoranalysis.image.io.generator.raster.objmask.rgb.RGBObjMaskGenerator;
import org.anchoranalysis.image.io.input.NamedChnlsInput;
import org.anchoranalysis.image.io.input.series.NamedChnlCollectionForSeries;
import org.anchoranalysis.image.io.stack.StackCollectionOutputter;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.objmask.properties.ObjMaskWithPropertiesCollection;
import org.anchoranalysis.image.sgmn.SgmnFailedException;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.NamedImgStackCollection;
import org.anchoranalysis.io.bean.objmask.writer.RGBOutlineWriter;
import org.anchoranalysis.io.generator.collection.IterableGeneratorWriter;
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
	public void doStack( NamedChnlsInput inputObject, int seriesIndex, BoundOutputManagerRouteErrors outputManager, LogErrorReporter logErrorReporter, String stackDescriptor, ExperimentExecutionArguments expArgs ) throws JobExecutionException {
		
		try {
			SgmnObjMaskCollection sgmnDup = sgmn.duplicateBean();
			
			assert( sgmnDup != null );
			
			ProgressReporter progressReporter = ProgressReporterNull.get();
			
			NamedChnlCollectionForSeries ncc = inputObject.createChnlCollectionForSeries(0, progressReporter );

			NamedImgStackCollection stackCollection = new NamedImgStackCollection();
			StackCollectionOutputter.copyFrom(ncc, stackCollection, progressReporter);
		
			sgmnDup.initRecursive(logErrorReporter);
			
			// Test that values have opened correctly
			Chnl chnlIn = stackCollection.getException(ImgStackIdentifiers.INPUT_IMAGE).getChnl(0);
			outputManager.getWriterCheckIfAllowed().write(
				outputNameOriginal,
				() -> new ChnlGenerator(chnlIn,"original")
			);
			
			NamedProviderStore<ObjMaskCollection> objMaskCollectionStore = new LazyEvaluationStore<>(logErrorReporter, "objMaskCollection");
			
			ObjMaskCollection objs = sgmnDup.sgmn(stackCollection, objMaskCollectionStore, null, randomNumberGenerator.create(), expArgs, logErrorReporter, outputManager );

			// Write out the results as a subfolder
			IterableGeneratorWriter.writeSubfolder(
				outputManager,
				"maskChnl",
				"maskChnl",
				() -> new ObjMaskChnlGenerator(chnlIn),
				objs.asList(),
				true
			);
			

			// Write out the results as a subfolder
			IterableGeneratorWriter.writeSubfolder(
				outputManager,
				"mask",
				"mask",
				() -> new ObjMaskGenerator(255, chnlIn.getDimensions().getRes() ),
				objs.asList(),
				true
			);			

			outputManager.getWriterCheckIfAllowed().write(
				"outline",
				() -> {
					try {
						return new RGBObjMaskGenerator(
							new RGBOutlineWriter(),
							new ObjMaskWithPropertiesCollection(objs),
							DisplayStack.create(chnlIn),
							outputManager.getOutputWriteSettings().genDefaultColorIndex(objs.size())
						);
					} catch (CreateException | OperationFailedException e) {
						throw new ExecuteException(e);
					}
				}
			);
			
		} catch (SgmnFailedException | RasterIOException | InitException | GetOperationFailedException | BeanDuplicateException e) {
			throw new JobExecutionException(e);
		}
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
