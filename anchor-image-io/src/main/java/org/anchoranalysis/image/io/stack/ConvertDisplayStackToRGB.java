/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.stack;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.spatial.box.BoundingBox;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvertDisplayStackToRGB {

    public static RGBStack convert(DisplayStack background) {

        try {
            if (background.getNumberChannels() == 1) {
                return new RGBStack(
                        background.createChannel(0, true),
                        background.createChannel(0, true),
                        background.createChannel(0, true));
            } else if (background.getNumberChannels() == 3) {
                return new RGBStack(background.deriveStack(true));
            } else {
                throw new AnchorImpossibleSituationException();
            }
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    public static RGBStack convertCropped(DisplayStack background, BoundingBox box) {

        try {
            if (background.getNumberChannels() == 1) {
                Channel channel = background.extractChannelForBoundingBox(0, box);
                return new RGBStack(channel, channel.duplicate(), channel.duplicate());
            } else if (background.getNumberChannels() == 3) {
                return new RGBStack(
                        background.extractChannelForBoundingBox(0, box),
                        background.extractChannelForBoundingBox(1, box),
                        background.extractChannelForBoundingBox(2, box));
            } else {
                throw new AnchorImpossibleSituationException();
            }
        } catch (IncorrectImageSizeException e) {
            // This should not be possible
            throw new AnchorImpossibleSituationException();
        }
    }
}
