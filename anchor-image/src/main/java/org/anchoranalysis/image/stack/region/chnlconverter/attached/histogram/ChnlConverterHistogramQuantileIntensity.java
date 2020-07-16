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
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverterToByteScaleByMaxValue;

// Scales by a quantile of the intensity values of an image
public class ChnlConverterHistogramQuantileIntensity
        implements ChnlConverterAttached<Histogram, ByteBuffer> {

    private VoxelBoxConverterToByteScaleByMaxValue voxelBoxConverter;
    private double quantile = 1.0;
    private ChannelConverterToUnsignedByte delegate;

    public ChnlConverterHistogramQuantileIntensity(double quantile) {
        // Initialise with a dummy value
        voxelBoxConverter = new VoxelBoxConverterToByteScaleByMaxValue(1);
        this.quantile = quantile;
        delegate = new ChannelConverterToUnsignedByte(voxelBoxConverter);
    }

    @Override
    public void attachObject(Histogram hist) throws OperationFailedException {

        int maxValue = hist.quantile(quantile);
        voxelBoxConverter.setMaxValue(maxValue);
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
