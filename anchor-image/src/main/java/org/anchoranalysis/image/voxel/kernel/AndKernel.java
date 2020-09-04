/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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

package org.anchoranalysis.image.voxel.kernel;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.voxel.Voxels;

public class AndKernel extends BinaryKernel {

    private BinaryKernel kernel1;
    private BinaryKernel kernel2;

    public AndKernel(BinaryKernel kernel1, BinaryKernel kernel2) {
        super(kernel1.getSize());
        this.kernel1 = kernel1;
        this.kernel2 = kernel2;
    }

    @Override
    public void init(Voxels<ByteBuffer> in) {
        kernel1.init(in);
        kernel2.init(in);
    }

    @Override
    public void notifyZChange(LocalSlices inSlices, int z) {
        kernel1.notifyZChange(inSlices, z);
        kernel2.notifyZChange(inSlices, z);
    }

    @Override
    public boolean acceptPoint(int ind, Point3i point) {
        return kernel1.acceptPoint(ind, point) && kernel2.acceptPoint(ind, point);
    }
}
