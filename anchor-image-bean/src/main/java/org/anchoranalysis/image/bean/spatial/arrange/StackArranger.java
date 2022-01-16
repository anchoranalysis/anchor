/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.spatial.arrange;

import java.util.Iterator;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.ArrangeStackException;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackArrangement;
import org.anchoranalysis.image.bean.nonbean.spatial.arrange.StackCopierAtBox;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.core.stack.RGBStack;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.resizer.VoxelsResizer;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;

/**
 * Base class for a method that determines positions for {@link RGBStack}s when combined onto a
 * single plane.
 *
 * @author Owen Feehan
 */
public abstract class StackArranger extends AnchorBean<StackArranger> {

    /**
     * Creates a new {@link RGBStack} of voxel data type <i>unsigned byte</i> that is a combination
     * of other {@link RGBStack}s.
     *
     * @param stacks the images that will be combined.
     * @param resizer resizes an image, if it doesn't match the bounding-box size for the copy
     *     operation.
     * @return a newly created {@link RGBStack} with all {@code stacks} copied into it.
     * @throws ArrangeStackException if there are more {@code stacks} than can be arranged, or
     *     otherwise an error occurs arranging them.
     */
    public RGBStack combine(List<RGBStack> stacks, VoxelsResizer resizer)
            throws ArrangeStackException {
        return combine(
                stacks, resizer, ChannelFactory.instance().get(UnsignedByteVoxelType.INSTANCE));
    }

    /**
     * Creates a new {@link RGBStack} that is a combination of other {@link RGBStack}s.
     *
     * @param stacks the images that will be combined.
     * @param resizer resizes an image, if it doesn't match the bounding-box size for the copy
     *     operation.
     * @param factory the factory used to create the new {@link RGBStack}.
     * @return a newly created {@link RGBStack} with all {@code stacks} copied into it.
     * @throws ArrangeStackException if there are more {@code stacks} than can be arranged, or
     *     otherwise an error occurs arranging them.
     */
    public RGBStack combine(
            List<RGBStack> stacks, VoxelsResizer resizer, ChannelFactorySingleType factory)
            throws ArrangeStackException {

        Iterator<Extent> extentIterator = stacks.stream().map(RGBStack::extent).iterator();

        StackArrangement arrangement = arrangeStacks(extentIterator);

        if (extentIterator.hasNext()) {
            throw new ArrangeStackException("There are more stacks than can be arranged.");
        }

        RGBStack stackOut = new RGBStack(arrangement.extent(), factory);
        populateStacks(stacks, arrangement, resizer, stackOut.asStack());
        return stackOut;
    }

    /**
     * Arranges stacks to that they fit together in a single raster.
     *
     * @param extents the size of each respective stack for the arrangement.
     * @return bounding-boxes for each respective {@link RGBStack} in the unified plane.
     * @throws ArrangeStackException if a bounding-box cannot be determined for any stack.
     */
    public abstract StackArrangement arrangeStacks(Iterator<Extent> extents)
            throws ArrangeStackException;

    private static void populateStacks(
            Iterable<RGBStack> generatedImages,
            StackArrangement arrangement,
            VoxelsResizer resizer,
            Stack out)
            throws ArrangeStackException {

        int index = 0;
        for (RGBStack image : generatedImages) {
            BoundingBox box = arrangement.get(index++).getBox();
            if (!image.extent().equals(box.extent())) {
                // Resize image to match the box
                try {
                    image = image.mapChannel(channel -> channel.resizeXY(box.extent(), resizer));
                } catch (OperationFailedException e) {
                    throw new ArrangeStackException("Failed to resize an image before copying", e);
                }
            }
            StackCopierAtBox.copyImageInto(image.asStack(), out, box);
        }
    }
}
