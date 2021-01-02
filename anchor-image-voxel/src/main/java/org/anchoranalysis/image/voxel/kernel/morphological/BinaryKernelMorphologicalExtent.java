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

package org.anchoranalysis.image.voxel.kernel.morphological;

import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.kernel.KernelApplicationParameters;
import org.anchoranalysis.spatial.Extent;

public abstract class BinaryKernelMorphologicalExtent extends BinaryKernelMorphological {

    // START REQUIRED ARGUMENTS
    /**
     * The outcome that should occur for the kernel <b>if no neighbor fulfills the condition</b>
     * required by the kernel.
     */
    private final boolean unqualifiedOutcome;

    /**
     * The outcome that should occur for the kernel <b>if at least one neighbor fulfills the
     * condition</b> required by the kernel.
     */
    private final boolean qualifiedOutcome;
    // END REQUIRED ARGUMENTS

    protected Extent extent;

    /**
     * Create with an unqualified-outcome, what occurs if no no neighbor fulfills the condition
     * required by the kernel..
     *
     * <p>If a neighbor fulfills the condition, the outcome is assumed to be the opposite of this.
     *
     * <p>Thus if {@code unqualifiedOutcome==false} then the qualified-outcome is @{code true} and
     * vice-versa.
     *
     * @param unqualifiedOutcome the outcome that occurs if no neighbor fulfills the condition
     *     required by the kernel.
     */
    public BinaryKernelMorphologicalExtent(boolean unqualifiedOutcome) {
        this.unqualifiedOutcome = unqualifiedOutcome;
        this.qualifiedOutcome = !unqualifiedOutcome;
    }

    @Override
    public void init(Voxels<UnsignedByteBuffer> in, KernelApplicationParameters params) {
        this.extent = in.extent();
    }

    /**
     * The outcome that should occur for the kernel <b>if no neighbor fulfills the condition</b>
     * required by the kernel.
     */
    protected boolean isUnqualifiedOutcome() {
        return unqualifiedOutcome;
    }

    /**
     * The outcome that should occur for the kernel <b>if at least one neighbor fulfills the
     * condition</b> required by the kernel.
     */
    protected boolean isQualifiedOutcome() {
        return qualifiedOutcome;
    }
}
