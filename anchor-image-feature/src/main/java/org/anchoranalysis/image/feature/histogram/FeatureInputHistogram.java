/* (C)2020 */
package org.anchoranalysis.image.feature.histogram;

import java.util.Optional;
import org.anchoranalysis.feature.input.FeatureInputWithRes;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.histogram.Histogram;

public class FeatureInputHistogram implements FeatureInputWithRes {

    private Histogram histogram;
    private Optional<ImageResolution> res;

    /**
     * Constructor
     *
     * @param histogram
     * @param res can be NULL so features should expect it
     */
    public FeatureInputHistogram(Histogram histogram, Optional<ImageResolution> res) {
        super();
        this.histogram = histogram;
        this.res = res;
    }

    @Override
    public Optional<ImageResolution> getResOptional() {
        return res;
    }

    public Histogram getHistogram() {
        return histogram;
    }

    public void setHistogram(Histogram histogram) {
        this.histogram = histogram;
    }

    public void setRes(ImageResolution res) {
        this.res = Optional.of(res);
    }
}
