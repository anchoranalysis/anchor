/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.attached;

import java.nio.Buffer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.stack.region.chnlconverter.ChannelConverter;
import org.anchoranalysis.image.stack.region.chnlconverter.ConversionPolicy;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverter;

/**
 * Simply passes everything onto a ChnlConverter
 *
 * @author Owen Feehan
 * @param <S> attachment-type
 * @param <T> destination-type
 */
public class ChnlConverterAttachedSimple<S, T extends Buffer>
        implements ChnlConverterAttached<S, T> {

    private ChannelConverter<T> delegate;

    public ChnlConverterAttachedSimple(ChannelConverter<T> delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public Channel convert(Channel chnl, ConversionPolicy changeExisting) {
        return delegate.convert(chnl, changeExisting);
    }

    @Override
    public void attachObject(S obj) {
        // Nothing happens
    }

    @Override
    public VoxelBoxConverter<T> getVoxelBoxConverter() {
        return delegate.getVoxelBoxConverter();
    }
}
