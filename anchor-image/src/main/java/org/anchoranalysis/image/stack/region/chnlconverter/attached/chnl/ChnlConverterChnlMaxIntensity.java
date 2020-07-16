/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.attached.chnl;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.stack.region.chnlconverter.ChannelConverterToUnsignedByte;
import org.anchoranalysis.image.stack.region.chnlconverter.ConversionPolicy;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.ChnlConverterAttached;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverter;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverterToByteScaleByMaxValue;

public class ChnlConverterChnlMaxIntensity implements ChnlConverterAttached<Channel, ByteBuffer> {

    private VoxelBoxConverterToByteScaleByMaxValue voxelBoxConverter;

    private ChannelConverterToUnsignedByte delegate;

    public ChnlConverterChnlMaxIntensity() {
        // Initialise with a dummy value
        voxelBoxConverter = new VoxelBoxConverterToByteScaleByMaxValue(1);

        delegate = new ChannelConverterToUnsignedByte(voxelBoxConverter);
    }

    @Override
    public void attachObject(Channel obj) {

        int maxValue = obj.getVoxelBox().any().ceilOfMaxPixel();
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
