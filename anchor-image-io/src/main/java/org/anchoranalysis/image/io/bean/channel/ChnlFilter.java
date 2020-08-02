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
import org.anchoranalysis.image.io.chnl.ChannelGetter;
import org.anchoranalysis.image.io.input.ImageInitParamsFactory;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bound.BoundIOContext;

// Applies a filter to a particular channel
// Uses a ChnlProvider initialised with a stack called "input_chnl"
public class ChnlFilter extends AnchorBean<ChnlFilter> implements ChannelGetter {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String channelName;

    @BeanField @Getter @Setter private ChannelProvider channel;
    // END BEAN PROPERTIES

    private ChannelGetter chnlCollection;

    private BoundIOContext context;

    public void init(ChannelGetter chnlCollection, BoundIOContext context) {
        this.chnlCollection = chnlCollection;
        this.context = context;
    }

    @Override
    public Channel getChannel(String name, int t, ProgressReporter progressReporter)
            throws GetOperationFailedException {

        try {
            if (!name.equals(channelName)) {
                return chnlCollection.getChannel(name, t, progressReporter);
            }

            ChannelProvider chnlProviderDup = channel.duplicateBean();

            Channel chnlIn = chnlCollection.getChannel(name, t, progressReporter);

            ImageInitParams soImage = ImageInitParamsFactory.create(context);
            soImage.addToStackCollection("input_chnl", new Stack(chnlIn));

            chnlProviderDup.initRecursive(soImage, context.getLogger());

            return chnlProviderDup.create();

        } catch (InitException
                | OperationFailedException
                | CreateException
                | BeanDuplicateException e) {
            throw new GetOperationFailedException(name, e);
        }
    }

    @Override
    public boolean hasChannel(String chnlName) {
        return chnlCollection.hasChannel(chnlName);
    }
}
