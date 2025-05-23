/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.channel.input;

import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.channel.Channel;

/**
 * Retrieves a {@link Channel} by name from a particular time-point.
 *
 * @author Owen Feehan
 */
public interface ChannelGetter {

    /**
     * Does a particular channel-name exist?
     *
     * @param channelName the name of the channel to check.
     * @return true iff the channel-name exists.
     */
    boolean hasChannel(String channelName);

    /**
     * Retrieve the {@link Channel} corresponding to {@code channelName} at a particular time-frame.
     *
     * @param channelName the name of the {@link Channel}.
     * @param timeIndex the index (beginning at 0) of the frame.
     * @param logger logger to write informative messes or non-fatal errors.
     * @return the retrieved {@link Channel}.
     * @throws GetOperationFailedException if no channel exists with {@code channelName} at
     *     time-point {@code timeIndex}.
     */
    Channel getChannel(String channelName, int timeIndex, Logger logger)
            throws GetOperationFailedException;
}
