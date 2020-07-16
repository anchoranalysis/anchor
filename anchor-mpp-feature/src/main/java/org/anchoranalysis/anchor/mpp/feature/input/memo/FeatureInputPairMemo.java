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

package org.anchoranalysis.anchor.mpp.feature.input.memo;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;

@EqualsAndHashCode(callSuper = true)
public class FeatureInputPairMemo extends FeatureInputNRG {

    private VoxelizedMarkMemo obj1;
    private VoxelizedMarkMemo obj2;

    public FeatureInputPairMemo(
            VoxelizedMarkMemo obj1, VoxelizedMarkMemo obj2, NRGStackWithParams nrgStack) {
        super(Optional.of(nrgStack));
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    public VoxelizedMarkMemo getObj1() {
        return obj1;
    }

    public void setObj1(VoxelizedMarkMemo obj1) {
        this.obj1 = obj1;
    }

    public VoxelizedMarkMemo getObj2() {
        return obj2;
    }

    public void setObj2(VoxelizedMarkMemo obj2) {
        this.obj2 = obj2;
    }
}
