package org.anchoranalysis.test.image;

/*-
 * #%L
 * anchor-test-image
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

import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.io.output.bound.BoundIOContext;
import org.anchoranalysis.test.LoggingFixture;

import static org.mockito.Mockito.*;

import java.nio.file.Path;

public class BoundIOContextFixture {

	private BoundIOContextFixture() {}
	
	public static BoundIOContext withSuppressedLogger() {
		return withLogger(
			LoggingFixture.suppressedLogErrorReporter()
		);
	}
	
	public static BoundIOContext withLogger(Logger logger) {
		BoundIOContext out = mock(BoundIOContext.class);
		when(out.getLogger()).thenReturn(logger);
		when(out.getLogReporter()).thenReturn(logger.messageLogger());
		when(out.getErrorReporter()).thenReturn(logger.errorReporter());
		return out;
	}
	
	public static BoundIOContext withSuppressedLogger( Path modelDir ) {
		BoundIOContext out = withSuppressedLogger();
		when(out.getModelDirectory()).thenReturn(modelDir);
		return out;
	}
}
