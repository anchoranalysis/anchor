/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.attached.chnl;

import java.nio.Buffer;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.histogram.HistogramFactory;
import org.anchoranalysis.image.stack.region.chnlconverter.ConversionPolicy;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.ChnlConverterAttached;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverter;

@RequiredArgsConstructor
public class ChnlConverterDelegateToHistogram<T extends Buffer>
        implements ChnlConverterAttached<Channel, T> {

    private final ChnlConverterAttached<Histogram, T> delegate;

    @Override
    public void attachObject(Channel obj) throws OperationFailedException {

        try {
            Histogram hist = HistogramFactory.create(obj);
            delegate.attachObject(hist);

        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    @Override
    public Channel convert(Channel chnl, ConversionPolicy changeExisting) {
        return delegate.convert(chnl, changeExisting);
    }

    @Override
    public VoxelBoxConverter<T> getVoxelBoxConverter() {
        return delegate.getVoxelBoxConverter();
    }
}
