/*-
 * #%L
 * anchor-image-bean
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

package org.anchoranalysis.image.bean.provider;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.xml.exception.ProvisionFailedException;
import org.anchoranalysis.image.core.channel.Channel;

/**
 * Implementation of {@link ChannelProvider} that calls <b>two</b> {@link ChannelProvider}s that
 * must provide {@link Channel}s of the same dimensions.
 *
 * @author Owen Feehan
 */
public abstract class ChannelProviderBinary extends ChannelProvider {

    // START BEAN PROPERTIES
    /** The <b>first</b> delegate {@link ChannelProvider} that is called. */
    @BeanField @Getter @Setter private ChannelProvider channel1;

    /** The <b>second</b> delegate {@link ChannelProvider} that is called. */
    @BeanField @Getter @Setter private ChannelProvider channel2;
    // END BEAN PROPERTIES

    @Override
    public Channel get() throws ProvisionFailedException {

        Channel channel1Created = channel1.get();
        Channel channel2Created = channel2.get();

        if (!channel1Created.dimensions().equals(channel2Created.dimensions())) {
            throw new ProvisionFailedException("Dimensions of channels do not match");
        }

        return process(channel1Created, channel2Created);
    }

    /**
     * Creates a {@link Channel} given the two entities provided by the delegates.
     *
     * @param channel1 the entity provided by the first delegate.
     * @param channel2 the entity provided by the second delegate.
     * @return the created {@link Channel} that is returned by the provider.
     * @throws ProvisionFailedException if the provider cannot complete successfully.
     */
    protected abstract Channel process(Channel channel1, Channel channel2)
            throws ProvisionFailedException;
}
