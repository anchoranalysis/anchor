package org.anchoranalysis.image.io.input;

/*
 * #%L
 * anchor-image-io
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


import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.name.store.NamedProviderStore;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.stack.TimeSequence;
import org.anchoranalysis.io.input.InputFromManager;

/**
 * Base class for inputs which somehow eventually send up providing stacks, with or without names
 * 
 * @author Owen Feehan
 *
 */
public abstract class ProvidesStackInput implements InputFromManager {

	// Adds the current object to a NamedItemStore of ImgStack
	//
	// stackCollection: destination to add to
	// seriesNum: a seriesNum to add
	// t: a timepoint to add
	// progressReporter
	public abstract void addToStore( NamedProviderStore<TimeSequence> stackCollection, int seriesNum, ProgressReporter progressReporter ) throws OperationFailedException;
	
	public abstract void addToStoreWithName( String name, NamedProviderStore<TimeSequence> stackCollection, int seriesNum, ProgressReporter progressReporter ) throws OperationFailedException;
	
	public abstract int numFrames() throws OperationFailedException;
}