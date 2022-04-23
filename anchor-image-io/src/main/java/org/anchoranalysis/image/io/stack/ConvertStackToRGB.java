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
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.bean.displayer.StackDisplayer;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.stack.DisplayStack;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Converts a {@link Stack} or {@link DisplayStack} to a {@link RGBStack}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvertStackToRGB {

    /**
     * Converts all of a {@link DisplayStack} to a {@link RGBStack}.
     *
     * @param stack the stack to convert.
     * @param displayer how to convert {@code stack} to be displayed.
     * @param alwaysNew when true, new channels are always created. when false, they are only
     *     created if needed (e.g. if the voxel-data type is not already 8-bit).
     * @return a newly created {@link RGBStack} with exactly three channels, and intensity-values
     *     converted to 8-bit.
     * @throws CreateException cannot successfuly convert {@link Stack} to a {@link DisplayStack}
     *     (as an intermediate step).
     */
    public static RGBStack convert(Stack stack, StackDisplayer displayer, boolean alwaysNew)
            throws CreateException {
        return convert(displayer.deriveFrom(stack), alwaysNew);
    }

    /**
     * Converts all of a {@link DisplayStack} to a {@link RGBStack}.
     *
     * @param stack the stack to convert.
     * @param alwaysNew when true, new channels are always created. when false, they are only
     *     created if needed (e.g. if the voxel-data type is not already 8-bit).
     * @return a newly created {@link RGBStack} with identical voxels and size as {@code stack}.
     */
    public static RGBStack convert(DisplayStack stack, boolean alwaysNew) {

        try {
            if (stack.getNumberChannels() == 1) {
                return new RGBStack(
                        stack.createChannel(0, alwaysNew),
                        stack.createChannel(0, alwaysNew),
                        stack.createChannel(0, alwaysNew));
            } else {
                return new RGBStack(
                        stack.createChannel(0, alwaysNew),
                        stack.createChannel(1, alwaysNew),
                        stack.createChannel(2, alwaysNew));
            }
        } catch (IncorrectImageSizeException e) {
            throw new AnchorImpossibleSituationException();
        }
    }

    /**
     * Converts a bounding-box region in {@link DisplayStack} to a {@link RGBStack}.
     *
     * @param stack the stack, from which a portion is to be converted.
     * @param box the region in {@code stack} which is converted.
     * @return a newly created {@link RGBStack} with identical voxels and size as {@code stack}.
     */
    public static RGBStack convertCropped(DisplayStack stack, BoundingBox box) {

        try {
            if (stack.getNumberChannels() == 1) {
                Channel channel = stack.extractChannelForBoundingBox(0, box);
                return new RGBStack(channel, channel.duplicate(), channel.duplicate());
            } else {
                return new RGBStack(
                        stack.extractChannelForBoundingBox(0, box),
                        stack.extractChannelForBoundingBox(1, box),
                        stack.extractChannelForBoundingBox(2, box));
            }
        } catch (IncorrectImageSizeException e) {
            // This should not be possible
            throw new AnchorImpossibleSituationException();
        }
    }
}
