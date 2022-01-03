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
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.nonbean.segment.SegmentationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;

/**
 * Implementation of {@link SegmentChannelIntoObjects} that calls a <b>single</b> delegate {@link
 * SegmentChannelIntoObjects}.
 *
 * @author Owen Feehan
 */
public abstract class SegmentChannelIntoObjectsUnary extends SegmentChannelIntoObjects {

    // START BEAN PROPERTIES
    /** The delegate {@link SegmentChannelIntoObjects} that is called. */
    @BeanField @Getter @Setter private SegmentChannelIntoObjects segment;
    // END BEAN PROPERTIES

    @Override
    public ObjectCollection segment(
            Channel channel, Optional<ObjectMask> objectMask, Optional<ObjectCollection> seeds)
            throws SegmentationFailedException {
        return segment(channel, objectMask, seeds, segment);
    }

    /**
     * Creates an {@link ObjectCollection} given the segmentation-results provided by the delegate.
     *
     * @param channel the channel as passed to {@link #segment(Channel, Optional, Optional)}.
     * @param object the object-mask as passed to {@link #segment(Channel, Optional, Optional)}.
     * @param seeds the seeds as passed to {@link #segment(Channel, Optional, Optional)}.
     * @param upstreamSegmenter the delegate segmenter.
     * @return the created {@link ObjectCollection} that is returned by the segmentation.
     * @throws SegmentationFailedException if segmentation cannot complete successfully.
     */
    protected abstract ObjectCollection segment(
            Channel channel,
            Optional<ObjectMask> object,
            Optional<ObjectCollection> seeds,
            SegmentChannelIntoObjects upstreamSegmenter)
            throws SegmentationFailedException;
}
