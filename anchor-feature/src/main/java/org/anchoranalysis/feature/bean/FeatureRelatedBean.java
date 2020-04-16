package org.anchoranalysis.feature.bean;

import java.util.Arrays;
import java.util.List;

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
import org.anchoranalysis.bean.init.property.ExtractFromParam;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.bean.init.property.SimplePropertyDefiner;
import org.anchoranalysis.bean.shared.params.keyvalue.KeyValueParamsInitParams;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.feature.shared.SharedFeaturesInitParams;

/**
 * Beans-related to features that require initialization with {@link SharedFeaturesInitParams}
 * 
 * @author owen
 *
 * @param <T> bean-type
 */
public abstract class FeatureRelatedBean<T> extends InitializableBeanSimple<T,SharedFeaturesInitParams> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private transient SharedFeaturesInitParams soFeature;
	
	protected FeatureRelatedBean() {
		super(
			new PropertyInitializer<>( SharedFeaturesInitParams.class, paramExtracters() ),
			new SimplePropertyDefiner<SharedFeaturesInitParams>(SharedFeaturesInitParams.class)
		);
	}
	
	@Override
	public void onInit(SharedFeaturesInitParams soFeature) throws InitException {
		super.onInit(soFeature);
		this.soFeature = soFeature;
	}

	public SharedFeaturesInitParams getSharedObjects() {
		return soFeature;
	}
	
	private static List<ExtractFromParam<SharedFeaturesInitParams,?>> paramExtracters() {
		return Arrays.asList(
			new ExtractFromParam<>(
				KeyValueParamsInitParams.class,
				SharedFeaturesInitParams::getParams
			)
		);
	}
}