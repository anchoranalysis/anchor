package org.anchoranalysis.io.bean.input;

/*
 * #%L
 * anchor-io
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


import java.util.List;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.log.LogErrorReporter;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.io.error.AnchorIOException;
import org.anchoranalysis.io.input.InputFromManager;
import org.anchoranalysis.io.params.InputContextParams;

/**
 * Base class for describing the input to an experiment (i.e. which files etc. form the source of the
 *   experimenent)
 * 
 * @author Owen Feehan
 *
 * @param <T> input-type
 */
public abstract class InputManager<T extends InputFromManager> extends AnchorBean<InputManager<T>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8305744272662659794L;

	public abstract List<T> inputObjects(InputContextParams inputContext, ProgressReporter progressReporter, LogErrorReporter logger) throws AnchorIOException;

}
