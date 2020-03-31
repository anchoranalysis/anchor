package org.anchoranalysis.anchor.mpp.bean.init;

/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann la Roche
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

import org.anchoranalysis.core.error.reporter.ErrorReporter;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.log.LogReporter;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.core.random.RandomNumberGeneratorMersenneConstant;

public class GeneralInitParams {

	private RandomNumberGenerator re;
	private Path modelDir;
	private LogErrorReporter logErrorReporter;
	
	public GeneralInitParams(Path modelDir, LogErrorReporter logErrorReporter) {
		this(
			new RandomNumberGeneratorMersenneConstant(),
			modelDir,
			logErrorReporter
		);
	}
	
	public GeneralInitParams(RandomNumberGenerator re, Path modelDir, LogErrorReporter logErrorReporter) {
		super();
		this.re = re;
		this.modelDir = modelDir;
		this.logErrorReporter = logErrorReporter;
	}

	public RandomNumberGenerator getRe() {
		return re;
	}

	public Path getModelDir() {
		return modelDir;
	}

	public LogErrorReporter getLogErrorReporter() {
		return logErrorReporter;
	}

	public ErrorReporter getErrorReporter() {
		return logErrorReporter.getErrorReporter();
	}

	public LogReporter getLogReporter() {
		return logErrorReporter.getLogReporter();
	}
}
