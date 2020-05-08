package org.anchoranalysis.image.feature.session;

import java.util.Optional;

/*-
 * #%L
 * anchor-plugin-mpp-experiment
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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

import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureInitParams;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.feature.init.FeatureInitParamsSharedObjs;
import org.anchoranalysis.image.init.ImageInitParams;

public class InitParamsHelper {
	
	public static FeatureInitParams createInitParams( ImageInitParams so, Optional<NRGStackWithParams> nrgStack ) {
		
		Optional<KeyValueParams> kvp = nrgStack.map( n->n.getParams() );
				
		FeatureInitParams params;
		if (so!=null) {
			params = new FeatureInitParamsSharedObjs(so);
			params.setKeyValueParams(kvp);
		} else {
			params = new FeatureInitParams(kvp);
		}
		params.setNrgStack(
			nrgStack.map( n->n.getNrgStack() )
		);
		return params;
	}
}
