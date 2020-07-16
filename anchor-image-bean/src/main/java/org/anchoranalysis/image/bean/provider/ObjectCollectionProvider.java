/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.object.ObjectCollection;

@GroupingRoot
public abstract class ObjectCollectionProvider extends ImageBean<ObjectCollectionProvider>
        implements Provider<ObjectCollection> {

    @Override
    public abstract ObjectCollection create() throws CreateException;
}
