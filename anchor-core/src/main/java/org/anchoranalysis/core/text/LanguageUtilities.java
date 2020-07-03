package org.anchoranalysis.core.text;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/*
 * #%L
 * anchor-core
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

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class LanguageUtilities {

	/**
	 * Returns something or somethings depending on the number
	 * 
	 * @param number the number n
	 * @param word the something
	 * @return the string as above
	 */
	public static String pluralizeMaybe( long number, String word ) {
		if (number==1) {
			return word;
		} else {
			return pluralize(word);
		}
	}
	
	/**
	 * Returns 1 something or n somethings as is appropriate
	 * 
	 * @param number the number n
	 * @param word the something
	 * @return the string as above
	 */
	public static String prefixPluralizeMaybe( long number, String word ) {
		if (number==1) {
			return "1 " + word;
		} else {
			return prefixPluralize(number,word);
		}
	}
	
	/**
	 * Returns n somethings
	 * 
	 * @param number the number n
	 * @param word the something
	 * @return the string as above
	 */
	public static String prefixPluralize( long number, String word ) {
		return String.format("%d %s", number, pluralize(word) );
	}
	
	/**
	 * Given something, returns somethings
	 * 
	 * @param word the something
	 * @return the string as above
	 */
	public static String pluralize( String word ) {
		return String.format("%ss", word);
	}
	
}
