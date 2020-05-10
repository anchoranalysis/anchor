package org.anchoranalysis.image.bean.provider;

/*-
 * #%L
 * anchor-plugin-image
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

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.chnl.Chnl;

/**
 * A chnl-provider based-on two input chnl-providers that must be of the same dimensionality
 * 
 * @author Owen Feehan
 *
 */
public abstract class ChnlProviderTwo extends ChnlProvider {

	
	// START BEAN PROPERTIES
	@BeanField
	private ChnlProvider chnl1;
	
	@BeanField
	private ChnlProvider chnl2;
	// END BEAN PROPERTIES

	@Override
	public Chnl create() throws CreateException {
		
		Chnl chnlFirst = chnl1.create();
		Chnl chnlSecond = chnl2.create();
				
		if (!chnlFirst.getDimensions().equals(chnlSecond.getDimensions())) {
			throw new CreateException("Dimensions of channels do not match");
		}
		
		return process( chnlFirst, chnlSecond );
	}
	
	protected abstract Chnl process( Chnl chnl1, Chnl chnl2 ) throws CreateException;

	public ChnlProvider getChnl1() {
		return chnl1;
	}

	public void setChnl1(ChnlProvider chnl1) {
		this.chnl1 = chnl1;
	}

	public ChnlProvider getChnl2() {
		return chnl2;
	}

	public void setChnl2(ChnlProvider chnl2) {
		this.chnl2 = chnl2;
	}
}
