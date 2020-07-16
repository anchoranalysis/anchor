/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.extent.ImageDimensions;

public abstract class ImageDimProvider extends ImageBean<ImageDimProvider>
        implements Provider<ImageDimensions> {

    public abstract ImageDimensions create() throws CreateException;
}
