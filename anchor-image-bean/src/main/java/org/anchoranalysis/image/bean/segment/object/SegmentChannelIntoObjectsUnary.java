/* (C)2020 */
package org.anchoranalysis.image.bean.segment.object;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.nonbean.error.SegmentationFailedException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.seed.SeedCollection;

/**
 * A base-class for segmentations that takes another object-segmentation algorithm as a parameter.
 *
 * @author Owen Feehan
 */
public abstract class SegmentChannelIntoObjectsUnary extends SegmentChannelIntoObjects {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private SegmentChannelIntoObjects segment;
    // END BEAN PROPERTIES

    @Override
    public ObjectCollection segment(
            Channel channel, Optional<ObjectMask> mask, Optional<SeedCollection> seeds)
            throws SegmentationFailedException {
        return segment(channel, mask, seeds, segment);
    }

    protected abstract ObjectCollection segment(
            Channel chnl,
            Optional<ObjectMask> object,
            Optional<SeedCollection> seeds,
            SegmentChannelIntoObjects upstreamSegmentation)
            throws SegmentationFailedException;
}
