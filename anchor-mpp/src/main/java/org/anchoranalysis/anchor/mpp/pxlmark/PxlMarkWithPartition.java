package org.anchoranalysis.anchor.mpp.pxlmark;

/*-
 * #%L
 * anchor-mpp
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

import org.anchoranalysis.anchor.mpp.pixelpart.IndexByChnl;

/**
 * 
 * @author Owen Feehan
 *
 * @param <T> index-type
 */
public abstract class PxlMarkWithPartition<T> extends PxlMark {

	// Quick access to what is inside and what is outside
	protected final IndexByChnl<T> partitionList;
	
	public PxlMarkWithPartition() {
		partitionList = new IndexByChnl<>();
	}
	
	protected PxlMarkWithPartition(PxlMarkWithPartition<T> src) {
		// No duplication, only shallow copy (for now). This might change in future.
		this.partitionList = src.partitionList;
	}
}
