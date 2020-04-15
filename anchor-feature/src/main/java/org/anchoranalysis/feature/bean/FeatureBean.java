package org.anchoranalysis.feature.bean;

/*-
 * #%L
 * anchor-feature
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

import org.anchoranalysis.bean.init.InitializableBeanSimple;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.init.property.SimplePropertyDefiner;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;

public abstract class FeatureBean<T> extends InitializableBeanSimple<T,SharedFeaturesInitParams> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private transient SharedFeaturesInitParams soFeature;
	
	protected FeatureBean() {
		super( new Initializer(), new SimplePropertyDefiner<SharedFeaturesInitParams>(SharedFeaturesInitParams.class) );
	}
	
	@Override
	public void onInit(SharedFeaturesInitParams soFeature) throws InitException {
		super.onInit(soFeature);
		this.soFeature = soFeature;
	}

	private static class Initializer extends PropertyInitializer<SharedFeaturesInitParams> {

		public Initializer() {
			super( SharedFeaturesInitParams.class );
		}

		@Override
		public boolean execIfInheritsFrom(Object propertyValue, Object parent, LogErrorReporter logger)
				throws InitException {

			boolean succ = super.execIfInheritsFrom(propertyValue,parent, logger);
			
			if (succ) {
				return succ;
			}
			
			if (initMatchingPropertiesWith(propertyValue, parent, logger, KeyValueParamsInitParams.class, getParam().getParams())) {
				return true;
			}
			
			return false;
		}
	}

	public SharedFeaturesInitParams getSharedObjects() {
		return soFeature;
	}
}
