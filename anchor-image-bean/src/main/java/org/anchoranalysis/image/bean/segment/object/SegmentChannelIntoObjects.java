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
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.bean.segment.SegmentationBean;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.seed.SeedCollection;

/**
 * A base class for routines to segment a channel into one or more objects.
 *
 * <p>Some algorithms can be restricted by an object-mask on the channel.
 *
 * <p>Some algorithms can be influenced by passing a set of seeds.
 *
 * @author Owen Feehan
 */
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
            Channel channel, Optional<ObjectMask> objectMask, Optional<SeedCollection> seeds)
            throws SegmentationFailedException;

    protected static void checkUnsupportedObjectMask(Optional<ObjectMask> objectMask)
            throws SegmentationFailedException {
        if (objectMask.isPresent()) {
            throw new SegmentationFailedException("An object-mask is not supported for this operation");
        }
    }

    protected static void checkUnsupportedSeeds(Optional<SeedCollection> seeds)
            throws SegmentationFailedException {
        if (seeds.isPresent()) {
            throw new SegmentationFailedException("Seeds are not supported for this operation");
        }
    }

    protected static void checkUnsupported3D(Channel channel) throws SegmentationFailedException {
        if (channel.getDimensions().getZ() > 1) {
            throw new SegmentationFailedException(
                    "Z-stacks (3D) are not supported for this operation");
        }
    }
}
