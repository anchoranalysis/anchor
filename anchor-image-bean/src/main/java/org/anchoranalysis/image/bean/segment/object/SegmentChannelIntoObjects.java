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

package org.anchoranalysis.image.bean.segment.object;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.bean.nonbean.segment.SegmentationFailedException;
import org.anchoranalysis.image.bean.segment.SegmentationBean;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * A base class for algorithms to segment a channel into one or more objects.
 *
 * <p>Some algorithms can be restricted by an object-mask on the channel.
 *
 * <p>Some algorithms can be influenced by passing a set of seeds.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SegmentChannelIntoObjects
        extends SegmentationBean<SegmentChannelIntoObjects> {

    /**
     * Segments a channel to produce an object-collection.
     *
     * @param channel the channel to segment
     * @param objectMask an object-mask that can restrict which areas of the channel are considered.
     * @param seeds seeds that can be used to determine starting-points for segmentation regions.
     * @return a newly created collection of objects for each segment. The created objects will
     *     always exist inside the channel's extent.
     * @throws SegmentationFailedException if anything goes wrong during the segmentation.
     */
    public abstract ObjectCollection segment(
            Channel channel, Optional<ObjectMask> objectMask, Optional<ObjectCollection> seeds)
            throws SegmentationFailedException;

    /**
     * Throws an exception if {@code object} is present, as it is unsupported by the implementation.
     *
     * @param objectMask an optional {@link ObjectMask}.
     * @throws SegmentationFailedException if {@code objectMask} is present.
     */
    protected static void checkUnsupportedObjectMask(Optional<ObjectMask> objectMask)
            throws SegmentationFailedException {
        if (objectMask.isPresent()) {
            throw new SegmentationFailedException(
                    "An object-mask is not supported for this operation");
        }
    }

    /**
     * Throws an exception if {@code seeds} is present, as it is unsupported by the implementation.
     *
     * @param seeds an optional {@link ObjectMask}.
     * @throws SegmentationFailedException if {@code seeds} is present.
     */
    protected static void checkUnsupportedSeeds(Optional<ObjectCollection> seeds)
            throws SegmentationFailedException {
        if (seeds.isPresent()) {
            throw new SegmentationFailedException("Seeds are not supported for this operation");
        }
    }

    /**
     * Throws an exception if the channel has more than one z-slice, as 3D is unsupported by the
     * implementation.
     *
     * @param channel the channel that have multiple z-slices (i.e. is 3D) or a single z-slice (i.e.
     *     is 2D).
     * @throws SegmentationFailedException if {@code channel} is 3D.
     */
    protected static void checkUnsupported3D(Channel channel) throws SegmentationFailedException {
        if (channel.dimensions().z() > 1) {
            throw new SegmentationFailedException(
                    "Z-stacks (3D) are not supported for this operation");
        }
    }
}
