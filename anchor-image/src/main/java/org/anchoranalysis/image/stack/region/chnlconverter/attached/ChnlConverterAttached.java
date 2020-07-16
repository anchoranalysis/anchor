/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.attached;

import java.nio.Buffer;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.stack.region.chnlconverter.ConversionPolicy;
import org.anchoranalysis.image.stack.region.chnlconverter.voxelbox.VoxelBoxConverter;

/**
 * A ChnlConverter that has been permanently attached to a particular object (to give more
 * information for the conversion)
 *
 * @author Owen Feehan
 * @param <S> attachment-type
 * @param <T> destination-type
 */
public interface ChnlConverterAttached<S, T extends Buffer> {

    void attachObject(S obj) throws OperationFailedException;

    Channel convert(Channel chnl, ConversionPolicy changeExisting);

    VoxelBoxConverter<T> getVoxelBoxConverter();
}
