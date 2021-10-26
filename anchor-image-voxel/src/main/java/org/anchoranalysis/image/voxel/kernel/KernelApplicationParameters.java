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

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.image.voxel.binary.BinaryVoxels;

/**
 * Parameters used for applying a {@link Kernel} to a {@link BinaryVoxels}.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor
public class KernelApplicationParameters {

    /**
     * How to handle voxels that appear in a neighborhood that lies outside the scene boundaries.
     */
    private final OutsideKernelPolicy outsideKernelPolicy;

    /** Whether to additionally apply the kernel along the Z dimension, as well as X and Y? */
    private final boolean useZ;

    /**
     * When true, any parts of the neighborhood that lie outside the scene are not considered in
     * kernel neighborhoods.
     * 
     * @return true if parts of the neighborhood that lie outside the scene should be ignored.
     */
    public boolean isIgnoreOutside() {
        return outsideKernelPolicy.isIgnoreOutside();
    }

    /**
     * In cases where {@link #isIgnoreOutside()} is false, then whether to treat voxels that lie outside the scene
     * as <i>on</i> (if true) or <i>off</i> (if false).
     * 
     * @return true if voxels lying outside the scene should be treated as <i>on</i> in the above circumstances, otherwise they are treated as <i>off</i>.
     */
    public boolean isOutsideOn() {
        return outsideKernelPolicy.isOutsideOn();
    }

    /**
     * True only when voxels outside the scene should <b>not be ignored</i> and considered as <i>off</i>.
     * 
     * @return true iff both conditions above are true.
     */
    public boolean isOutsideOffUnignored() {
        return !isIgnoreOutside() && !isOutsideOn();
    }

    /**
     * True only when voxels outside the scene should <b>not be ignored</i> and considered as <i>on</i>.
     * 
     * @return true iff both conditions above are true.
     */
    public boolean isOutsideOnUnignored() {
        return !isIgnoreOutside() && isOutsideOn();
    }
}
