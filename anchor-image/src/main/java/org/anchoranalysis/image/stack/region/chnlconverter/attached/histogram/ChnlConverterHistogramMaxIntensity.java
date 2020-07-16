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

public class ChnlConverterHistogramMaxIntensity
        implements ChnlConverterAttached<Histogram, ByteBuffer> {

    private VoxelBoxConverterToByteScaleByMaxValue voxelBoxConverter;

    private ChannelConverterToUnsignedByte delegate;

    public ChnlConverterHistogramMaxIntensity() {
        // Initialise with a dummy value
        voxelBoxConverter = new VoxelBoxConverterToByteScaleByMaxValue(1);

        delegate = new ChannelConverterToUnsignedByte(voxelBoxConverter);
    }

    @Override
    public void attachObject(Histogram hist) throws OperationFailedException {

        int maxValue = hist.calcMax();
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
