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

import org.anchoranalysis.core.error.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ConvertDisplayStackToRGB {

    public static RGBStack convert(DisplayStack background) {

        try {
            if (background.getNumberChannels() == 1) {
                return new RGBStack(
                        background.createChnlDuplicate(0),
                        background.createChnlDuplicate(0),
                        background.createChnlDuplicate(0));
            } else if (background.getNumberChannels() == 3) {
                return new RGBStack(background.createStackDuplicate());
            } else {
                throw new AnchorImpossibleSituationException();
            }
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    public static RGBStack convertCropped(DisplayStack background, BoundingBox bbox) {

        try {
            if (background.getNumberChannels() == 1) {
                Channel chnl = background.createChannelDuplicateForBoundingBox(0, bbox);
                return new RGBStack(chnl, chnl.duplicate(), chnl.duplicate());
            } else if (background.getNumberChannels() == 3) {
                return new RGBStack(
                        background.createChannelDuplicateForBoundingBox(0, bbox),
                        background.createChannelDuplicateForBoundingBox(1, bbox),
                        background.createChannelDuplicateForBoundingBox(2, bbox));
            } else {
                throw new AnchorImpossibleSituationException();
            }
        } catch (IncorrectImageSizeException e) {
            // This should not be possible
            throw new AnchorImpossibleSituationException();
        }
    }
}
