/* (C)2020 */
package org.anchoranalysis.io.bioformats;

import org.anchoranalysis.image.channel.Channel;

/**
 * Selects a destination-channel for the byte-copier, given an index
 *
 * <p>As a byte-array can contain more than one channel, an index determines which
 * destination-channel is selected
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface DestChnlForIndex {

    Channel get(int index);
}
