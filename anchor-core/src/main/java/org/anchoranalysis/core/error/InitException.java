package org.anchoranalysis.core.error;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

/*-
 * #%L
 * anchor-core
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

/**
 * An exception that occurs when initializing something, and it
 *   doesn't succeed
 * 
 * @author Owen Feehan
 *
 */
public class InitException extends AnchorFriendlyCheckedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6589491763755816321L;

	public InitException(String string) {
		super(string);
	}

	public InitException(Throwable exc ) {
		super( exc );
	}
	
	public InitException(String message, Throwable cause) {
		super(message, cause);
	}

	public static InitException createOrReuse( Throwable exc ) {
		// If it's an initialization error, we don't create a new one but re-throw
		//  as frequently initialization errors will pass through a recursive train
		if (exc.getCause() instanceof InitException) {
			return (InitException) exc.getCause();
		} else {
			return new InitException(exc);
		}
	}
		
}
