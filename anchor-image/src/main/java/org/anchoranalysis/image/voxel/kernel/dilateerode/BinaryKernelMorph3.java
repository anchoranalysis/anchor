package org.anchoranalysis.image.voxel.kernel.dilateerode;

/*-
 * #%L
 * anchor-image
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

import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.voxel.kernel.BinaryKernel;
import org.anchoranalysis.image.voxel.kernel.LocalSlices;

public abstract class BinaryKernelMorph3 extends BinaryKernel {

	protected final BinaryValuesByte bv;
	protected final boolean outsideAtThreshold;
	
	protected LocalSlices inSlices;
	
	public BinaryKernelMorph3(BinaryValuesByte bv, boolean outsideAtThreshold) {
		super(3);
		this.bv = bv;
		this.outsideAtThreshold = outsideAtThreshold;
	}
	

	@Override
	public void notifyZChange(LocalSlices inSlices, int z) {
		this.inSlices = inSlices;
	}
}
