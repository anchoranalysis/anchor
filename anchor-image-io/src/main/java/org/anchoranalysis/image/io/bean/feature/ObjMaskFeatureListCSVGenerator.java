package org.anchoranalysis.image.io.bean.feature;

import java.nio.file.Path;


/*
 * #%L
 * anchor-image-io
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

import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.unit.SpatialConversionUtilities.UnitSuffix;
import org.anchoranalysis.feature.bean.Feature;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.calc.results.ResultsVectorCollection;
import org.anchoranalysis.feature.io.csv.FeatureListCSVGeneratorVertical;
import org.anchoranalysis.feature.io.csv.TableCSVGenerator;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.FeatureSession;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.feature.shared.SharedFeatureMulti;
import org.anchoranalysis.image.feature.bean.objmask.CenterOfGravity;
import org.anchoranalysis.image.feature.bean.objmask.NumVoxels;
import org.anchoranalysis.image.feature.bean.physical.convert.ConvertToPhysicalDistance;
import org.anchoranalysis.image.feature.objmask.FeatureInputSingleObj;
import org.anchoranalysis.image.objmask.ObjMask;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.image.orientation.DirectionVector;
import org.anchoranalysis.io.generator.Generator;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.generator.csv.CSVGenerator;
import org.anchoranalysis.io.output.bean.OutputWriteSettings;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;

/**
 * 
 * @author owen
 *
 * @param <T> feature calculation params
 */
class ObjMaskFeatureListCSVGenerator extends CSVGenerator implements IterableGenerator<ObjMaskCollection> {

	private TableCSVGenerator<ResultsVectorCollection> delegate;
	
	private ObjMaskCollection objs;
	
	private FeatureList<FeatureInputSingleObj> features;
	
	private FeatureInitParams paramsInit;	// Optional initialization parameters
	private SharedFeatureMulti<FeatureInputSingleObj> sharedFeatures = new SharedFeatureMulti<>();
	
	private NRGStackWithParams nrgStack;
	private LogErrorReporter logErrorReporter;
	
	public ObjMaskFeatureListCSVGenerator( FeatureList<FeatureInputSingleObj> features, NRGStackWithParams nrgStack, LogErrorReporter logErrorReporter ) {
		super("objMaskFeatures");
		this.nrgStack = nrgStack;
		this.logErrorReporter = logErrorReporter;
		this.features = createFullFeatureList( features, logErrorReporter );
		
		delegate = new FeatureListCSVGeneratorVertical( "objMaskFeatures", features.createNames() );
	}

	@Override
	public Generator getGenerator() {
		return this;
	}
	
	@Override
	public void writeToFile(OutputWriteSettings outputWriteSettings,
			Path filePath) throws OutputWriteFailedException {
				
		ResultsVectorCollection rvc;
		try {
			 FeatureCalculatorMulti<FeatureInputSingleObj> session = FeatureSession.with(
				features,
				paramsInit,
				sharedFeatures,
				logErrorReporter
			);
			
			// We calculate a results vector for each object, across all features in memory. This is more efficient
			rvc = new ResultsVectorCollection();
			for( ObjMask om : objs ) {
				rvc.add( 
					session.calcSuppressErrors(
						createParams(om, nrgStack),
						logErrorReporter.getErrorReporter()
					)
				);
			}
		} catch (FeatureCalcException e) {
			throw new OutputWriteFailedException(e);
		}
		
		delegate.setIterableElement(rvc);
		delegate.writeToFile(outputWriteSettings, filePath);
	}

	@Override
	public ObjMaskCollection getIterableElement() {
		return objs;
	}

	@Override
	public void setIterableElement(ObjMaskCollection element) {
		this.objs = element;
	}

	private void addFeature( Feature<FeatureInputSingleObj> feature, String prefix, FeatureList<FeatureInputSingleObj> featureList ) throws BeanMisconfiguredException {
		featureList.addWithCustomName( feature.duplicateBean(), prefix + feature.getFriendlyName() );
	}
	
	// Puts in some extra descriptive features at the start
	private FeatureList<FeatureInputSingleObj> createFullFeatureList( FeatureList<FeatureInputSingleObj> features, LogErrorReporter logger ) {
		
		FeatureList<FeatureInputSingleObj> featuresAll = new FeatureList<>();
		
		Feature<FeatureInputSingleObj> cogX = new CenterOfGravity("x");
		Feature<FeatureInputSingleObj> cogY = new CenterOfGravity("y");
		Feature<FeatureInputSingleObj> cogZ = new CenterOfGravity("z");
		
		featuresAll.addWithCustomName( cogX, "x" );
		featuresAll.addWithCustomName( cogY, "y" );
		featuresAll.addWithCustomName( cogZ, "z" );
		
		addConvertedFeature( featuresAll, cogX, new DirectionVector(1, 0, 0), "x_p" );
		addConvertedFeature( featuresAll, cogY, new DirectionVector(0, 1, 0), "y_p" );
		addConvertedFeature( featuresAll, cogZ, new DirectionVector(0, 0, 1), "z_p" );

		featuresAll.addWithCustomName( new NumVoxels(), "numVoxels" );
		
		for (Feature<FeatureInputSingleObj> f : features) {
			try {
				addFeature(f, "", featuresAll);
			} catch (BeanMisconfiguredException e) {
				logger.getErrorReporter().recordError(this.getClass(), e);
			}
		}
		
		return featuresAll;
	}
	
	private static void addConvertedFeature( FeatureList<FeatureInputSingleObj> featuresAll, Feature<FeatureInputSingleObj> feature, DirectionVector dir, String name ) {
		featuresAll.addWithCustomName( convert(feature, dir), name );
	}
	
	private static Feature<FeatureInputSingleObj> convert( Feature<FeatureInputSingleObj> feature, DirectionVector dir ) {
		return new ConvertToPhysicalDistance<>(feature, UnitSuffix.MICRO, dir);
	}
	
	public FeatureInitParams getParamsInit() {
		return paramsInit;
	}

	public void setParamsInit(FeatureInitParams paramsInit) {
		this.paramsInit = paramsInit;
	}

	public SharedFeatureMulti<FeatureInputSingleObj> getSharedFeatures() {
		return sharedFeatures;
	}

	public void setSharedFeatures(SharedFeatureMulti<FeatureInputSingleObj> sharedFeatures) {
		this.sharedFeatures = sharedFeatures;
	}
	
	private static FeatureInputSingleObj createParams(ObjMask om, NRGStackWithParams nrgStack) {
		return new FeatureInputSingleObj(om, nrgStack);
	}
}
