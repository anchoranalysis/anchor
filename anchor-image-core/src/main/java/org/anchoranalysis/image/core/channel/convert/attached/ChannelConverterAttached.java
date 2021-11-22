/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.core.channel.convert.attached;

import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.channel.convert.ConversionPolicy;
import org.anchoranalysis.image.voxel.convert.VoxelsConverter;

/**
 * A channel-converter that has been permanently associated with a particular object.
 *
 * <p>This object can provide necessary parameters (e.g. ranges of voxel intensities) for the
 * conversion.
 *
 * @author Owen Feehan
 * @param <S> type of object to which the channel-convert is attached.
 * @param <T> buffer-type the voxels will be converted <b>to</b>.
 */
public interface ChannelConverterAttached<S, T> {

    /**
     * Associate the convert with a particular object.
     *
     * @param object the object to associate the converter with.
     * @throws OperationFailedException if the association cannot be completed successfully.
     */
    void attachObject(S object) throws OperationFailedException;

    /**
     * Converts the voxel-type in a {@link Channel}.
     *
     * @param channel the channel whose voxels will be converted.
     * @param changeExisting if ture, the are voxels in-place, preserving the existing {@link
     *     Channel} object. Otherwise a new {@link Channel} is created.
     * @return the existing {@link Channel} or newly created one, as per above.
     */
    Channel convert(Channel channel, ConversionPolicy changeExisting);

    /**
     * Converts voxels to have type voxel-type {@code T}.
     *
     * @return the converter.
     */
    VoxelsConverter<T> getVoxelsConverter();
}
