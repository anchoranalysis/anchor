package org.anchoranalysis.annotation.io.wholeimage.findable;

/*-
 * #%L
 * anchor-annotation-io
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

import org.anchoranalysis.core.log.LogErrorReporter;

/**
 * A positive-result when an object is found
 * 
 * @author owen
 *
 * @param <T>
 */
public class Found<T> extends Findable<T> {

	private T obj;

	/**
	 * The found object.
	 * 
	 * @param obj
	 */
	public Found(T obj) {
		super();
		this.obj = obj;
	}

	public T getObj() {
		return obj;
	}

	@Override
	public boolean logIfFailure(String name, LogErrorReporter logErrorReporter) {
		return true;
	}

	@Override
	public T getOrNull() {
		return obj;
	}
}