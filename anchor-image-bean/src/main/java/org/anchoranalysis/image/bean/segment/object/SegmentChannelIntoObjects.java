/* (C)2020 */
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
 * <p>Some algorithms can be restricted by a mask on the channel.
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
     * @param mask a mask that can restrict which areas of the channel are considered.
     * @param seeds seeds that can be used to determine starting-points for segmentation regions.
     * @return a newly created collection of objects for each segment. The created objects will
     *     always exist inside the channel's extent.
     * @throws SegmentationFailedException if anything goes wrong during the segmentation.
     */
    public abstract ObjectCollection segment(
            Channel channel, Optional<ObjectMask> mask, Optional<SeedCollection> seeds)
            throws SegmentationFailedException;

    protected static void checkUnsupportedMask(Optional<ObjectMask> mask)
            throws SegmentationFailedException {
        if (mask.isPresent()) {
            throw new SegmentationFailedException("A mask is not supported for this operation");
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
