/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import org.anchoranalysis.bean.Provider;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.histogram.Histogram;

@GroupingRoot
public abstract class HistogramProvider extends ImageBean<HistogramProvider>
        implements Provider<Histogram> {

    public abstract Histogram create() throws CreateException;
}
