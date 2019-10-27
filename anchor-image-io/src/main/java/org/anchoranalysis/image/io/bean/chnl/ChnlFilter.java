package org.anchoranalysis.image.io.bean.chnl;

import org.anchoranalysis.bean.AnchorBean;

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


import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.bean.provider.ChnlProvider;
import org.anchoranalysis.image.chnl.Chnl;
import org.anchoranalysis.image.init.ImageInitParams;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.chnl.ChnlGetter;
import org.anchoranalysis.image.stack.Stack;

// Applies a filter to a particular channel
// Uses a ChnlProvider initialised with a stack called "input_chnl"
public class ChnlFilter extends AnchorBean<ChnlFilter> implements ChnlGetter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// START BEAN PROPERTIES
	@BeanField
	private String chnlName;
	
	@BeanField
	private ChnlProvider chnlProvider;
	// END BEAN PROPERTIES
	
	private ChnlGetter chnlCollection;

	private LogErrorReporter logErrorReporter;
	private RandomNumberGenerator re;
	
	public void init( ChnlGetter chnlCollection, LogErrorReporter logErrorReporter, RandomNumberGenerator re ) {
		this.chnlCollection = chnlCollection;
		this.logErrorReporter = logErrorReporter;
		this.re = re;
	}
	
	@Override
	public Chnl getChnl( String name, int t, ProgressReporter progressReporter ) throws GetOperationFailedException {
		
		try {
			if( !name.equals(chnlName) ) {
				return chnlCollection.getChnl(name, t, progressReporter);
			}
			
			ChnlProvider chnlProviderDup = chnlProvider.duplicateBean();
			
			Chnl chnlIn = chnlCollection.getChnl(name, t, progressReporter);
			
			ImageInitParams soImage = ImageInitParams.create(logErrorReporter, re);
			soImage.addToStackCollection("input_chnl", new Stack(chnlIn) );
			
			chnlProviderDup.initRecursive(soImage, logErrorReporter);
			
			return chnlProviderDup.create();
			
		} catch (InitException | OperationFailedException | RasterIOException | CreateException | BeanDuplicateException e) {
			throw new GetOperationFailedException(e);
		}
	}

	public String getChnlName() {
		return chnlName;
	}

	public void setChnlName(String chnlName) {
		this.chnlName = chnlName;
	}

	public ChnlProvider getChnlProvider() {
		return chnlProvider;
	}

	public void setChnlProvider(ChnlProvider chnlProvider) {
		this.chnlProvider = chnlProvider;
	}

	@Override
	public boolean hasChnl(String chnlName) {
		return chnlCollection.hasChnl(chnlName);
	}
	
}
