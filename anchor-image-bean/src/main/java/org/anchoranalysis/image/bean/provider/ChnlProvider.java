/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.stack.Stack;

@GroupingRoot
public abstract class ChnlProvider extends BeanImgStackProvider<ChnlProvider, Channel> {

    public abstract Channel create() throws CreateException;

    public Stack createStack() throws CreateException {
        return new Stack(create());
    }
}
