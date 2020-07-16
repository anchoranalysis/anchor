/* (C)2020 */
package org.anchoranalysis.image.feature.bean.pixelwise;

import java.util.List;
import java.util.Optional;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.params.KeyValueParams;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.image.bean.ImageBean;
import org.anchoranalysis.image.histogram.Histogram;

/** Calculates a per-pixel score */
public abstract class PixelScore extends ImageBean<PixelScore> {

    /**
     * Initializes the pixels-score.
     *
     * <p>Must be called before calc()
     *
     * @param histograms one or more histograms associated with this calculation
     * @param keyValueParams optional key-value params associated with this calculation
     * @throws InitException if anything goes wrong
     */
    public void init(List<Histogram> histograms, Optional<KeyValueParams> keyValueParams)
            throws InitException {
        // TO be overridden if needed
    }

    public abstract double calc(int[] pixelVals) throws FeatureCalcException;
}
