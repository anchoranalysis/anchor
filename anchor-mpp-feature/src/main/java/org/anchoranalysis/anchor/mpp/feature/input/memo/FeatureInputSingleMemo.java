/*-
 * #%L
 * anchor-mpp-feature
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
/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.input.memo;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

@EqualsAndHashCode(callSuper = true)
public class FeatureInputSingleMemo extends FeatureInputNRG {

    private VoxelizedMarkMemo pxlPartMemo;

    public FeatureInputSingleMemo(VoxelizedMarkMemo pxlPartMemo, NRGStackWithParams nrgStack) {
        this(pxlPartMemo, Optional.of(nrgStack));
    }

    public FeatureInputSingleMemo(
            VoxelizedMarkMemo pxlPartMemo, Optional<NRGStackWithParams> nrgStack) {
        super(nrgStack);
        this.pxlPartMemo = pxlPartMemo;
    }

    public VoxelizedMarkMemo getPxlPartMemo() {
        return pxlPartMemo;
    }

    public void setPxlPartMemo(VoxelizedMarkMemo pxlPartMemo) {
        this.pxlPartMemo = pxlPartMemo;
    }
}
