/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.bean.cfg;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.feature.input.FeatureInputWithRes;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;

public class FeatureInputCfg implements FeatureInputWithRes {

    private Cfg cfg;
    private Optional<ImageDimensions> dim;

    public FeatureInputCfg(Cfg cfg, Optional<ImageDimensions> dim) {
        super();
        this.cfg = cfg;
        this.dim = dim;
    }

    @Override
    public Optional<ImageResolution> getResOptional() {
        return dim.map(ImageDimensions::getRes);
    }

    public Cfg getCfg() {
        return cfg;
    }

    public void setCfg(Cfg cfg) {
        this.cfg = cfg;
    }

    public Optional<ImageDimensions> getDimensions() {
        return dim;
    }
}
