package org.anchoranalysis.feature.bean.list;

/*
 * #%L
 * anchor-feature
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
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.shared.regex.RegEx;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.name.provider.NamedProviderGetException;
import org.anchoranalysis.feature.calc.params.FeatureInput;

/**
 * Selects a number of features of shared-features matching against a regular-expression
 * 
 * @author Owen Feehan
 *
 */
public class FeatureListProviderSelectFromShared<T extends FeatureInput> extends FeatureListProviderReferencedFeatures<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3698511957662882985L;
	
	// START BEAN PROPERTIES
	@BeanField @OptionalBean
	private RegEx match = null;
	// END BEAN PROPERTIES
	
	@Override
	public FeatureList<T> create() throws CreateException {
		
		FeatureList<T> out = new FeatureList<>();
		
		for( String key : getSharedObjects().getFeatureListSet().keys() ) {
			if( match==null || match.matchStr(key)!=null ) {
				try {
					out.add(
						getSharedObjects().getSharedFeatureSet().getException(key).downcast()
					);
				} catch (NamedProviderGetException e) {
					throw new CreateException(e);
				}
			}
		}
		
		return out;
	}

	public RegEx getMatch() {
		return match;
	}

	public void setMatch(RegEx match) {
		this.match = match;
	}



}
