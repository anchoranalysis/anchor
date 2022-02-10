package org.anchoranalysis.image.bean.displayer;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.convert.attached.ChannelConverterAttached;
import org.anchoranalysis.image.core.channel.convert.attached.channel.IntensityRange;
import org.anchoranalysis.image.core.channel.convert.attached.channel.UpperLowerQuantileIntensity;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * Implementation of {@link StackDisplayer} that converts to unsigned-8bit by taking the upper and
 * lower quantiles of the intensity range.
 *
 * <p>Within the two ranges, values are mapped linearly onto the full unsigned 8-bit range.
 *
 * @author Owen Feehan
 */
public class IntensityQuantiles extends StackDisplayer {

    // START BEAN PROPERTIES
    /** The lower quantile to use as the <b>lower</b>-limit for the intensity range to display. */
    @BeanField @Getter @Setter private double quantileLower = 0.0001;

    /** The lower quantile to use as the <b>upper</b>-limit for the intensity range to display. */
    @BeanField @Getter @Setter private double quantileUpper = 0.9999;
    // END BEAN PROPERTIES

    @Override
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        super.checkMisconfigured(defaultInstances);
        if (quantileLower >= quantileUpper) {
            throw new BeanMisconfiguredException(
                    String.format(
                            "The lower-quantile (%f) is not lower than the upper-quantile (%f)",
                            quantileLower, quantileUpper));
        }
    }

    @Override
    protected ChannelConverterAttached<Channel, UnsignedByteBuffer> createConverterFor(
            VoxelDataType dataType) {
        // If the bit-depth is managable to create a histogram, let's use it to establish quantiles
        // TODO introduce an alternative structure to the histogram for calculating the quantiles
        // when not 8-bit.
        if (dataType.bitDepth() <= UnsignedShortVoxelType.BIT_DEPTH) {
            return new UpperLowerQuantileIntensity(quantileLower, quantileUpper);
        } else {
            // Otherwise for float or 32-bit types, a histogram would be too big in memory. So let's
            // use a straightforward min or max.
            return new IntensityRange();
        }
    }
}
