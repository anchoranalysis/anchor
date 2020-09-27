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

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.image.bean.nonbean.init.ImageInitParams;
import org.anchoranalysis.image.bean.provider.ChannelProvider;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.io.channel.ChannelGetter;
import org.anchoranalysis.image.io.input.ImageInitParamsFactory;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.outputter.InputOutputContext;

/**
 * Applies a filter to a particular channel
 *
 * <p>Forms input from a stack called <i>input_channel</i>
 *
 * @author Owen Feehan
 */
public class ChannelFilter extends AnchorBean<ChannelFilter> implements ChannelGetter {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String channelName;

    @BeanField @Getter @Setter private ChannelProvider channel;
    // END BEAN PROPERTIES

    private ChannelGetter channels;

    private InputOutputContext context;

    public void init(ChannelGetter channels, InputOutputContext context) {
        this.channels = channels;
        this.context = context;
    }

    @Override
    public Channel getChannel(String name, int timeIndex, ProgressReporter progressReporter)
            throws GetOperationFailedException {

        try {
            if (!name.equals(channelName)) {
                return channels.getChannel(name, timeIndex, progressReporter);
            }

            ChannelProvider providerDuplicated = channel.duplicateBean();

            Channel channelSelected = channels.getChannel(name, timeIndex, progressReporter);
            initProvider(providerDuplicated, channelSelected);

            return providerDuplicated.create();

        } catch (InitException | CreateException | BeanDuplicateException e) {
            throw new GetOperationFailedException(name, e);
        }
    }

    @Override
    public boolean hasChannel(String channelName) {
        return channels.hasChannel(channelName);
    }

    private void initProvider(ChannelProvider provider, Channel channel) throws InitException {
        ImageInitParams soImage = ImageInitParamsFactory.create(context);
        try {
            soImage.addToStackCollection("input_channel", new Stack(channel));
        } catch (OperationFailedException e) {
            throw new InitException(e);
        }
        provider.initRecursive(soImage, context.getLogger());
    }
}
