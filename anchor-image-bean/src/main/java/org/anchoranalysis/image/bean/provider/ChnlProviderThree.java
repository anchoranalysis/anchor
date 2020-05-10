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

public abstract class ChnlProviderThree extends ChnlProvider {

	// START BEAN PROPERTIES
	@BeanField
	private ChnlProvider chnlProvider1;
	
	@BeanField
	private ChnlProvider chnlProvider2;
	
	@BeanField
	private ChnlProvider chnlProvider3;
	// END BEAN PROPERTIES
		
	@Override
	public Chnl create() throws CreateException {

		return process(
			chnlProvider1.create(),
			chnlProvider2.create(),
			chnlProvider3.create()
		);
	}
	
	protected abstract Chnl process( Chnl chnl1, Chnl chnl2, Chnl chnl3 ) throws CreateException;

	public ChnlProvider getChnlProvider1() {
		return chnlProvider1;
	}

	public void setChnlProvider1(ChnlProvider chnlProvider1) {
		this.chnlProvider1 = chnlProvider1;
	}

	public ChnlProvider getChnlProvider2() {
		return chnlProvider2;
	}

	public void setChnlProvider2(ChnlProvider chnlProvider2) {
		this.chnlProvider2 = chnlProvider2;
	}


	public ChnlProvider getChnlProvider3() {
		return chnlProvider3;
	}

	public void setChnlProvider3(ChnlProvider chnlProvider3) {
		this.chnlProvider3 = chnlProvider3;
	}
}