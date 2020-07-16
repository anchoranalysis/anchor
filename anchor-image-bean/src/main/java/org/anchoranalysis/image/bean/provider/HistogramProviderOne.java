/* (C)2020 */
package org.anchoranalysis.image.bean.provider;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.histogram.Histogram;

public abstract class HistogramProviderOne extends HistogramProvider {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private HistogramProvider histogramProvider;
    // END BEAN PROPERTIES

    @Override
    public Histogram create() throws CreateException {
        return createFromHistogram(histogramProvider.create());
    }

    protected abstract Histogram createFromHistogram(Histogram hist) throws CreateException;
}
