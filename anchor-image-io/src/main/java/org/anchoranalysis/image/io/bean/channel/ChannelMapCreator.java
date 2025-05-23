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

package org.anchoranalysis.image.io.bean.channel;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.core.channel.Channel; // NOSONAR
import org.anchoranalysis.image.io.channel.input.ChannelMap;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;

/**
 * Creates a {@link ChannelMap} for a particular {@link OpenedImageFile}.
 *
 * @author Owen Feehan
 */
public abstract class ChannelMapCreator extends AnchorBean<ChannelMapCreator> {

    /**
     * Creates the {@link ChannelMap}.
     *
     * @param openedFile an opened image-file containing {@link Channel}s.
     * @param logger a logger for recording informative or error messages.
     * @return a {@link ChannelMap} that assigns names to channels in {@code openedFile}.
     * @throws CreateException if the {@link ChannelMap} cannot be successfully created.
     */
    public abstract ChannelMap create(OpenedImageFile openedFile, Logger logger)
            throws CreateException;
}
