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

import java.nio.file.Path;

import org.anchoranalysis.core.log.LogErrorReporter;

/**
 * A negative-result when an object is NOT found at a particular location
 * 
 * @author owen
 *
 * @param <T>
 */
public class NotFound<T> extends Findable<T> {

	private Path path;
	private String reason;

	/**
	 * Constructor
	 * 
	 * @param path the path an object was not found at.
	 */
	public NotFound(Path path, String reason) {
		super();
		this.path = path;
		this.reason = reason;
	}

	public Path getPath() {
		return path;
	}

	public String getReason() {
		return reason;
	}

	@Override
	public boolean logIfFailure(String name, LogErrorReporter logErrorReporter) {

		logErrorReporter.getLogReporter().logFormatted(
			"Cannot find %s: %s at %s",
			name,
			reason,
			path
		);
		
		return false;
	}

	@Override
	public T getOrNull() {
		return null;
	}
	
	
}