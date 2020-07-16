/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.attached.chnl;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.histogram.ChnlConverterHistogramQuantileIntensity;

// Scales by a quantile of the intensity values of an image
public class ChnlConverterChnlQuantileIntensity
        extends ChnlConverterDelegateToHistogram<ByteBuffer> {

    public ChnlConverterChnlQuantileIntensity(double quantile) {
        super(new ChnlConverterHistogramQuantileIntensity(quantile));
    }
}
