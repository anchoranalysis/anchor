package org.anchoranalysis.core.error.friendly;

/*-
 * #%L
 * anchor-core
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

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates over an exception and all it's causes skipping friendly-exceptions with empty messages 
 * 
 * <p>Note it won't skip such an exception if it is the final one in the chain</p>
 * 
 * @author owen
 *
 */
class ExceptionToPrintIterator implements Iterator<Throwable> {

	private Throwable current;
	
	public ExceptionToPrintIterator(Throwable root) {
		this.current = skipIfNotAcceptable( root );
	}
	
	@Override
	public boolean hasNext() {
		return !ExceptionTypes.isFinal(current);
	}

	@Override
	public Throwable next() {
		
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		
		current = skipIfNotAcceptable( current.getCause() );
		return current;
	}
	
	private static Throwable skipIfNotAcceptable( Throwable e ) {
		
		// Skip any exception with any empty message if it's an AnchorFriendlyCheckedException and not final
		// The loop is guaranteed to end, as we'll certainly eventually meet a final exception
		while (ExceptionTypes.hasEmptyMessage(e) && ExceptionTypes.isFriendly(e) && !ExceptionTypes.isFinal(e)) {
			e = e.getCause();
		}
		
		return e;
	}
}
