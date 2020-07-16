/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.attached.histogram;

import java.nio.ByteBuffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.stack.region.chnlconverter.ChannelConverterToUnsignedByte;
import org.anchoranalysis.image.stack.region.chnlconverter.ConversionPolicy;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.ChnlConverterAttached;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverter;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverterToByteScaleByMinMaxValue;

// Scales by a quantile of the intensity values of an image
public class ChnlConverterHistogramUpperLowerQuantileIntensity
        implements ChnlConverterAttached<Histogram, ByteBuffer> {

    private VoxelBoxConverterToByteScaleByMinMaxValue voxelBoxConverter;
    private double quantileLower = 0.0;
    private double quantileUpper = 1.0;
    private double scaleLower = 0.0;
    private double scaleUpper = 0.0;
    private ChannelConverterToUnsignedByte delegate;

    public ChnlConverterHistogramUpperLowerQuantileIntensity(
            double quantileLower, double quantileUpper) {
        this(quantileLower, quantileUpper, 1.0, 1.0);
    }

    public ChnlConverterHistogramUpperLowerQuantileIntensity(
            double quantileLower, double quantileUpper, double scaleLower, double scaleUpper) {
        // Initialise with a dummy value
        voxelBoxConverter = new VoxelBoxConverterToByteScaleByMinMaxValue(0, 1);
        this.quantileLower = quantileLower;
        this.quantileUpper = quantileUpper;
        this.scaleUpper = scaleUpper;
        this.scaleLower = scaleLower;

        delegate = new ChannelConverterToUnsignedByte(voxelBoxConverter);
    }

    @Override
    public void attachObject(Histogram hist) throws OperationFailedException {

        int minValue = scaleQuantile(hist, quantileLower, scaleLower);
        int maxValue = scaleQuantile(hist, quantileUpper, scaleUpper);
        voxelBoxConverter.setMinMaxValues(minValue, maxValue);
    }

    private int scaleQuantile(Histogram hist, double quantile, double scaleFactor)
            throws OperationFailedException {
        return (int) Math.round(hist.quantile(quantile) * scaleFactor);
    }

    @Override
    public Channel convert(Channel chnl, ConversionPolicy changeExisting) {
        return delegate.convert(chnl, changeExisting);
    }

    @Override
    public VoxelBoxConverter<ByteBuffer> getVoxelBoxConverter() {
        return voxelBoxConverter;
    }
}
