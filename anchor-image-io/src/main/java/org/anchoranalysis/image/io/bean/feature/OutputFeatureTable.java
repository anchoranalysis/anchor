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
import java.util.Optional;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsProvider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.bean.list.FeatureListFactory;
import org.anchoranalysis.feature.bean.provider.FeatureProvider;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.bean.provider.ObjectCollectionProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.feature.init.FeatureInitParamsShared;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.output.bound.BoundIOContext;

// Doesn't change the objects, just uses a generator to output a feature list as a CSV
public class OutputFeatureTable extends ImageBean<OutputFeatureTable> {

	// START BEAN PROPERTIES
	@BeanField
	private ObjectCollectionProvider objs;
	
	@BeanField
	private List<FeatureProvider<FeatureInputSingleObject>> listFeatureProvider = new ArrayList<>();
	
	@BeanField @OptionalBean
	private StackProvider stackProviderNRG;
	
	@BeanField @OptionalBean
	private KeyValueParamsProvider keyValueParamsProvider;
	
	@BeanField
	private String outputName = "objsFeatureList";
	// END BEAN PROPERTIES

	public void output(	BoundIOContext context ) throws IOException {
		
		// Early exit if we're not allowed output anything anyway
		if (!context.getOutputManager().isOutputAllowed(getOutputName())) {
			return;
		}
		
		try {
			ObjectCollection objsCollection = objs.create();
			
			FeatureList<FeatureInputSingleObject> features = FeatureListFactory.fromProviders(listFeatureProvider);
			
			if (features.size()==0) {
				throw new IOException("No features are set");
			}
			
			// Init
			FeatureInitParamsShared paramsInit = new FeatureInitParamsShared( getInitializationParameters() );
			paramsInit.setKeyValueParams(
				Optional.of(
					createKeyValueParams()
				)
			);
			
			// Create NRG stack
			final NRGStackWithParams nrgStack = stackProviderNRG!=null ? new NRGStackWithParams( stackProviderNRG.create() ) : null;
				
			context.getOutputManager().getWriterCheckIfAllowed().write(
				outputName,
				() -> createGenerator(paramsInit, nrgStack, objsCollection, features, context.getLogger() )
			);
			
		} catch (CreateException e) {
			throw new IOException(e);
		}
	}
	
	private ObjMaskFeatureListCSVGenerator createGenerator(
		FeatureInitParamsShared paramsInit,
		NRGStackWithParams nrgStack,
		ObjectCollection objsCollection,
		FeatureList<FeatureInputSingleObject> features,
		Logger logger
	) {
		ObjMaskFeatureListCSVGenerator generator = new ObjMaskFeatureListCSVGenerator(
			features,
			nrgStack,
			logger
		);
		generator.setParamsInit(paramsInit);
		generator.setSharedFeatures(
			getInitializationParameters().getFeature().getSharedFeatureSet()
		);
		generator.setIterableElement(objsCollection);
		return generator;
	}
	
	private KeyValueParams createKeyValueParams() throws CreateException {
		if (keyValueParamsProvider!=null) {
			return keyValueParamsProvider.create();
		} else {
			return new KeyValueParams();
		}
	}
	
	public ObjectCollectionProvider getObjs() {
		return objs;
	}


	public void setObjs(ObjectCollectionProvider objs) {
		this.objs = objs;
	}


	public List<FeatureProvider<FeatureInputSingleObject>> getListFeatureProvider() {
		return listFeatureProvider;
	}


	public void setListFeatureProvider(List<FeatureProvider<FeatureInputSingleObject>> listFeatureProvider) {
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
