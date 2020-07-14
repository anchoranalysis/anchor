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
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.bean.provider.ObjectCollectionProvider;
import org.anchoranalysis.image.bean.provider.stack.StackProvider;
import org.anchoranalysis.image.feature.object.input.FeatureInputSingleObject;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.io.output.bound.BoundIOContext;

import lombok.Getter;
import lombok.Setter;

// Doesn't change the objects, just uses a generator to output a feature list as a CSV
public class OutputFeatureTable extends ImageBean<OutputFeatureTable> {

	private static final String OUTPUT_NAME_OBJECTS_FEATURE_LIST = "objectsFeatureList";
	// START BEAN PROPERTIES
	@BeanField @Getter @Setter
	private ObjectCollectionProvider objects;
	
	@BeanField @Getter @Setter
	private List<FeatureProvider<FeatureInputSingleObject>> listFeatureProvider = new ArrayList<>();
	
	@BeanField @OptionalBean @Getter @Setter
	private StackProvider stackProviderNRG;
	
	@BeanField @OptionalBean @Getter @Setter
	private KeyValueParamsProvider keyValueParamsProvider;
	// END BEAN PROPERTIES

	public void output(	BoundIOContext context ) throws IOException {
		
		// Early exit if we're not allowed output anything anyway
		if (!context.getOutputManager().isOutputAllowed(OUTPUT_NAME_OBJECTS_FEATURE_LIST)) {
			return;
		}
		
		try {
			ObjectCollection objectCollection = objects.create();
			
			FeatureList<FeatureInputSingleObject> features = FeatureListFactory.fromProviders(listFeatureProvider);
			
			if (features.size()==0) {
				throw new IOException("No features are set");
			}
			
			// Init
			FeatureInitParams paramsInit = new FeatureInitParams(
				Optional.of(
					createKeyValueParams()
				),
				Optional.empty(),
				Optional.of(
					getInitializationParameters().getSharedObjects()
				)
			);
			
			// Create NRG stack
			final NRGStackWithParams nrgStack = stackProviderNRG!=null ? new NRGStackWithParams( stackProviderNRG.create() ) : null;
				
			context.getOutputManager().getWriterCheckIfAllowed().write(
				OUTPUT_NAME_OBJECTS_FEATURE_LIST,
				() -> createGenerator(
					paramsInit,
					nrgStack,
					objectCollection,
					features,
					context.getLogger()
				)
			);
			
		} catch (CreateException e) {
			throw new IOException(e);
		}
	}
	
	private ObjMaskFeatureListCSVGenerator createGenerator(
		FeatureInitParams paramsInit,
		NRGStackWithParams nrgStack,
		ObjectCollection objects,
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
		generator.setIterableElement(objects);
		return generator;
	}
	
	private KeyValueParams createKeyValueParams() throws CreateException {
		if (keyValueParamsProvider!=null) {
			return keyValueParamsProvider.create();
		} else {
			return new KeyValueParams();
		}
	}
}
