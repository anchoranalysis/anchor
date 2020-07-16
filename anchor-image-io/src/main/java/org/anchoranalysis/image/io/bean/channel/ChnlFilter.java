/* (C)2020 */
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
import org.anchoranalysis.image.bean.provider.ChnlProvider;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.io.chnl.ChnlGetter;
import org.anchoranalysis.image.io.input.ImageInitParamsFactory;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.io.output.bound.BoundIOContext;

// Applies a filter to a particular channel
// Uses a ChnlProvider initialised with a stack called "input_chnl"
public class ChnlFilter extends AnchorBean<ChnlFilter> implements ChnlGetter {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private String channelName;

    @BeanField @Getter @Setter private ChnlProvider channel;
    // END BEAN PROPERTIES

    private ChnlGetter chnlCollection;

    private BoundIOContext context;

    public void init(ChnlGetter chnlCollection, BoundIOContext context) {
        this.chnlCollection = chnlCollection;
        this.context = context;
    }

    @Override
    public Channel getChnl(String name, int t, ProgressReporter progressReporter)
            throws GetOperationFailedException {

        try {
            if (!name.equals(channelName)) {
                return chnlCollection.getChnl(name, t, progressReporter);
            }

            ChnlProvider chnlProviderDup = channel.duplicateBean();

            Channel chnlIn = chnlCollection.getChnl(name, t, progressReporter);

            ImageInitParams soImage = ImageInitParamsFactory.create(context);
            soImage.addToStackCollection("input_chnl", new Stack(chnlIn));

            chnlProviderDup.initRecursive(soImage, context.getLogger());

            return chnlProviderDup.create();

        } catch (InitException
                | OperationFailedException
                | CreateException
                | BeanDuplicateException e) {
            throw new GetOperationFailedException(e);
        }
    }

    @Override
    public boolean hasChnl(String chnlName) {
        return chnlCollection.hasChnl(chnlName);
    }
}
