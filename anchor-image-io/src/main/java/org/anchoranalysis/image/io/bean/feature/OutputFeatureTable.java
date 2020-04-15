package org.anchoranalysis.image.io.bean.feature;

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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.provider.FeatureProvider;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.bean.provider.ObjMaskProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.feature.init.FeatureInitParamsImageInit;
import org.anchoranalysis.image.feature.objmask.FeatureObjMaskParams;
import org.anchoranalysis.image.objmask.ObjMaskCollection;
import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

// Doesn't change the objects, just uses a generator to output a feature list as a CSV
public class OutputFeatureTable extends ImageBean<OutputFeatureTable> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private ObjMaskProvider objs;
	
	@BeanField
	private List<FeatureProvider<FeatureObjMaskParams>> listFeatureProvider = new ArrayList<>();
	
	@BeanField @OptionalBean
	private StackProvider stackProviderNRG;
	
	@BeanField @OptionalBean
	private KeyValueParamsProvider keyValueParamsProvider;
	
	@BeanField
	private String outputName = "objsFeatureList";
	// END BEAN PROPERTIES

	public void output(
		BoundOutputManagerRouteErrors outputManager,
		LogErrorReporter logErrorReporter
	) throws IOException {
		
		// Early exit if we're not allowed output anything anyway
		if (!outputManager.isOutputAllowed(getOutputName())) {
			return;
		}
		
		try {
			ObjMaskCollection objsCollection = objs.create();
			
			FeatureList<FeatureObjMaskParams> features = createFeatureList();
			
			if (features.size()==0) {
				throw new IOException("No features are set");
			}
			
			// Init
			FeatureInitParamsImageInit paramsInit = new FeatureInitParamsImageInit( getSharedObjects() );
			paramsInit.setKeyValueParams( createKeyValueParams() );
			
			// Create NRG stack
			final NRGStackWithParams nrgStack = stackProviderNRG!=null ? new NRGStackWithParams( stackProviderNRG.create() ) : null;
				
			outputManager.getWriterCheckIfAllowed().write(
				outputName,
				() -> createGenerator(paramsInit, nrgStack, objsCollection, features, logErrorReporter)
			);
			
		} catch (CreateException e) {
			throw new IOException(e);
		}
	}
	
	private ObjMaskFeatureListCSVGenerator createGenerator(
		FeatureInitParamsImageInit paramsInit,
		NRGStackWithParams nrgStack,
		ObjMaskCollection objsCollection,
		FeatureList<FeatureObjMaskParams> features,
		LogErrorReporter logErrorReporter
	) {
		ObjMaskFeatureListCSVGenerator generator = new ObjMaskFeatureListCSVGenerator(
			features,
			nrgStack,
			logErrorReporter
		);
		generator.setParamsInit(paramsInit);
		generator.setSharedFeatures( getSharedObjects().getFeature().getSharedFeatureSet().downcast() );
		generator.setIterableElement(objsCollection);
		return generator;
	}

	private FeatureList<FeatureObjMaskParams> createFeatureList() throws CreateException {
		FeatureList<FeatureObjMaskParams> out = new FeatureList<>();
		for( FeatureProvider<FeatureObjMaskParams> fp : listFeatureProvider) {
			out.add( fp.create() );
		}
		return out;
	}
	
	private KeyValueParams createKeyValueParams() throws CreateException {
		if (keyValueParamsProvider!=null) {
			return keyValueParamsProvider.create();
		} else {
			return new KeyValueParams();
		}
	}
	
	public ObjMaskProvider getObjs() {
		return objs;
	}


	public void setObjs(ObjMaskProvider objs) {
		this.objs = objs;
	}


	public List<FeatureProvider<FeatureObjMaskParams>> getListFeatureProvider() {
		return listFeatureProvider;
	}


	public void setListFeatureProvider(List<FeatureProvider<FeatureObjMaskParams>> listFeatureProvider) {
		this.listFeatureProvider = listFeatureProvider;
	}


	public StackProvider getStackProviderNRG() {
		return stackProviderNRG;
	}


	public void setStackProviderNRG(StackProvider stackProviderNRG) {
		this.stackProviderNRG = stackProviderNRG;
	}


	public KeyValueParamsProvider getKeyValueParamsProvider() {
		return keyValueParamsProvider;
	}


	public void setKeyValueParamsProvider(
			KeyValueParamsProvider keyValueParamsProvider) {
		this.keyValueParamsProvider = keyValueParamsProvider;
	}


	public String getOutputName() {
		return outputName;
	}


	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

}
