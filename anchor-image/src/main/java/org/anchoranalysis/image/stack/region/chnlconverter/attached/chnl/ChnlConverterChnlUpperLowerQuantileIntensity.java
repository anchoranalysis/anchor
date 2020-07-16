/* (C)2020 */
package org.anchoranalysis.image.stack.region.chnlconverter.attached.chnl;

import java.nio.ByteBuffer;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.histogram.ChnlConverterHistogramUpperLowerQuantileIntensity;

// Scales by a quantile of the intensity values of an image
public class ChnlConverterChnlUpperLowerQuantileIntensity
        extends ChnlConverterDelegateToHistogram<ByteBuffer> {

    public ChnlConverterChnlUpperLowerQuantileIntensity(
            double quantileLower, double quantileUpper) {
        super(new ChnlConverterHistogramUpperLowerQuantileIntensity(quantileLower, quantileUpper));
    }
}
