package org.anchoranalysis.bean.shared.regex;

import java.util.Optional;

/*
 * #%L
 * anchor-beans-shared
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


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.anchoranalysis.bean.annotation.BeanField;

public class RegExSimple extends RegEx {

	// START BEAN PROPERTIES
	@BeanField
	private String matchString;
	// END BEAN PROPERTIES

	public String getMatchString() {
		return matchString;
	}

	public void setMatchString(String matchString) {
		this.matchString = matchString;
	}

	@Override
	public Optional<String[]> matchStr( String str ) {
		
		Pattern p = Pattern.compile(matchString);
		
		Matcher matcher = p.matcher( str );
		
		if (!matcher.matches()) {
			//throw new IOException( String.format("RegEx string '%s' does not match '%s'", regEx, ff.getRemainderCombined() ));
			return Optional.empty();
		}
		return Optional.of(
			arrayFromMatcher(matcher)
		);
	}
	
	private String[] arrayFromMatcher( Matcher matcher ) {
		String[] arr = new String[matcher.groupCount()];
		for (int i=0; i<arr.length; i++) {
			arr[i] = matcher.group(i+1);
		}
		return arr;
	}

	@Override
	public String toString() {
		return String.format("regEx(%s)",matchString);
	}

}
