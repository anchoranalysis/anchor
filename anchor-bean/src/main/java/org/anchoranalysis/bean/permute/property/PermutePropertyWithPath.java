package org.anchoranalysis.bean.permute.property;

/*-
 * #%L
 * anchor-bean
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

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.permute.setter.PermutationSetter;
import org.anchoranalysis.bean.permute.setter.PermutationSetterException;
import org.anchoranalysis.bean.permute.setter.PermutationSetterList;
import org.anchoranalysis.bean.permute.setter.PermutationSetterUtilities;

/**
 * Base classes for PermuteProperty that requier a path
 * @author FEEHANO
 *
 */
public abstract class PermutePropertyWithPath<T> extends PermuteProperty<T> {


	// START BEAN PROPERTIES
	
	/**
	 * Either a direct property of a bean e.g. "someproperty"
	 * Or a nested-property with the children separated by full-stops:  e.g. "somechild1.somechild2.someproperty"
	 * 
	 * Children must be single multiplicity, and always be a subclass of IBean
	 */
	@BeanField
	private String propertyPath;
	
	/** Additional property paths that are also changed, along with the main propertyPath **/
	@BeanField @OptionalBean
	private StringSet additionalPropertyPaths;
	// END BEAN PROPERTIES
	

	// Searches through a list of property fields to find one that matches the propertyName
	@Override
	public PermutationSetter createSetter( AnchorBean<?> parentBean ) throws PermutationSetterException {
		
		PermutationSetterList out = new PermutationSetterList();
		
		addForPath(out, parentBean, propertyPath );
		
		// We add any additional paths
		if (additionalPropertyPaths!=null) {
			for( String additionalPath : additionalPropertyPaths.set() ) {
				addForPath(out, parentBean, additionalPath );
			}
		}
		
		return out;
	}
	
	private void addForPath( PermutationSetterList out, AnchorBean<?> parentBean, String path ) throws PermutationSetterException {
		out.add(
			PermutationSetterUtilities.createForSingle( parentBean, path )
		);
	}
	
	public String getPropertyPath() {
		return propertyPath;
	}

	public void setPropertyPath(String propertyPath) {
		this.propertyPath = propertyPath;
	}

	public StringSet getAdditionalPropertyPaths() {
		return additionalPropertyPaths;
	}

	public void setAdditionalPropertyPaths(StringSet additionalPropertyPaths) {
		this.additionalPropertyPaths = additionalPropertyPaths;
	}
	
}
