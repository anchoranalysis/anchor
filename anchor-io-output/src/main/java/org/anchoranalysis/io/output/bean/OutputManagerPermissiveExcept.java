package org.anchoranalysis.io.output.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.anchoranalysis.bean.NamedBean;

/*-
 * #%L
 * anchor-io-output
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

import org.anchoranalysis.bean.StringSet;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.io.output.bean.allowed.AllOutputAllowed;
import org.anchoranalysis.io.output.bean.allowed.OutputAllowed;
import org.anchoranalysis.io.output.bean.allowed.SpecificOutputDisallowed;


/**
 * Allows everything to be outputted except a particular list
 * 
 * @author Owen Feehan
 *
 */
public class OutputManagerPermissiveExcept extends OutputManagerWithPrefixer {

	// START BEAN PROPERTIES
	@BeanField
	private StringSet except;
	
	/** Ignores all these strings in the second-level (for respect keys) */
	@BeanField
	private List<NamedBean<StringSet>> exceptSecondLevel = new ArrayList<>();
	// END BEAN PROPERTIES
 
	// We cache the second-level map here
	private Map<String,OutputAllowed> mapSecondLevel = null;
	
	@Override
	public boolean isOutputAllowed(String outputName) {
		return !except.contains(outputName);
	}

	@Override
	public OutputAllowed outputAllowedSecondLevel(String key) {
		createSecondLevelMapIfNecessary();
		return mapSecondLevel.getOrDefault(key, new AllOutputAllowed());
	}
	
	private void createSecondLevelMapIfNecessary() {
		if (mapSecondLevel==null) {
			mapSecondLevel = createSecondLevelMap();
		}
	}
	
	private Map<String,OutputAllowed> createSecondLevelMap() {
		Map<String,OutputAllowed> map = new HashMap<>();
		for( NamedBean<StringSet> bean : exceptSecondLevel) {
			map.put(
				bean.getName(),
				new SpecificOutputDisallowed(bean.getItem())
			);
		}
		return map;
	}

	public StringSet getExcept() {
		return except;
	}

	public void setExcept(StringSet except) {
		this.except = except;
	}

	public List<NamedBean<StringSet>> getExceptSecondLevel() {
		return exceptSecondLevel;
	}

	public void setExceptSecondLevel(List<NamedBean<StringSet>> exceptSecondLevel) {
		this.exceptSecondLevel = exceptSecondLevel;
	}

}
