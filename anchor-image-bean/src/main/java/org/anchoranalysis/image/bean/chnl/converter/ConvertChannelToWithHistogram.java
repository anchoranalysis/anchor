/* (C)2020 */
package org.anchoranalysis.image.bean.chnl.converter;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.histogram.Histogram;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.ChnlConverterAttached;

public abstract class ConvertChannelToWithHistogram
        extends AnchorBean<ConvertChannelToWithHistogram> {

    public abstract ChnlConverterAttached<Histogram, ?> createConverter();
}
