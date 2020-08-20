/*-
 * #%L
 * anchor-test-image
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

package org.anchoranalysis.test.image;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.feature.nrg.NRGStack;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.test.image.ChannelFixture.IntensityFunction;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NRGStackFixture {

    public static NRGStackWithParams create(boolean big, boolean do3D) {

        Extent size = muxExtent(big, do3D);

        try {
            Stack stack = new Stack();
            addChannel(stack, size, ChannelFixture::sumMod);
            addChannel(stack, size, ChannelFixture::diffMod);
            addChannel(stack, size, ChannelFixture::multMod);

            NRGStack nrgStack = new NRGStack(stack);
            return new NRGStackWithParams(nrgStack);

        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    private static Extent muxExtent(boolean big, boolean do3D) {
        if (do3D) {
            return big ? ChannelFixture.LARGE_3D : ChannelFixture.MEDIUM_3D;
        } else {
            return big ? ChannelFixture.LARGE_2D : ChannelFixture.MEDIUM_2D;
        }
    }

    private static void addChannel(Stack stack, Extent size, IntensityFunction intensityFunction)
            throws IncorrectImageSizeException {
        stack.addChannel(ChannelFixture.createChannel(size, intensityFunction));
    }
}
