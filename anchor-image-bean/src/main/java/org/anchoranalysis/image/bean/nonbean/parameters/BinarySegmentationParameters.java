/* (C)2020 */
package org.anchoranalysis.image.bean.nonbean.parameters;

import java.util.Optional;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.histogram.Histogram;

/** Parameters that are optionally associated with BinarySgmn */
public class BinarySegmentationParameters {

    private Optional<Histogram> histogram;
    private Optional<ImageResolution> res;

    public BinarySegmentationParameters() {
        this.res = Optional.empty();
        this.histogram = Optional.empty();
    }

    public BinarySegmentationParameters(ImageResolution res) {
        this(res, Optional.empty());
    }

    /**
     * Constructor
     *
     * @param res image-resolution
     * @param histogram a histogram describing the intensity-values of the entire channel
     */
    public BinarySegmentationParameters(ImageResolution res, Optional<Histogram> histogram) {
        this.res = Optional.of(res);
        this.histogram = histogram;
    }

    public Optional<Histogram> getIntensityHistogram() {
        return histogram;
    }

    public Optional<ImageResolution> getRes() {
        return res;
    }
}
