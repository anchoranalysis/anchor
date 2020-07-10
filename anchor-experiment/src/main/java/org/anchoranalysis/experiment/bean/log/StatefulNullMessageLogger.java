package org.anchoranalysis.experiment.bean.log;

import org.anchoranalysis.core.log.MessageLogger;
import org.anchoranalysis.experiment.log.reporter.StatefulMessageLogger;

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

/**
 * Does nothing (i.e. simply ignores) with any messages logged.
 * <p>
 * This is kept distinct from {@link org.anchoranalysis.core.log.NullMessageLogger} as
 * it also implements the {@link StatefulMessageLogger} interface which exists in a more
 * downstream package as {@link MessageLogger}.
 * 
 * @author Owen Feehan
 *
 */
public class StatefulNullMessageLogger implements StatefulMessageLogger {

	@Override
	public void start() {
		// NOTHING TO DO
	}

	
	@Override
	public void log(String message) {
		// NOTHING TO DO		
	}

	@Override
	public void logFormatted(String formatString, Object... args) {
		// NOTHING TO DO		
	}

	@Override
	public void close(boolean successful) {
		// NOTHING TO DO		
	}

}