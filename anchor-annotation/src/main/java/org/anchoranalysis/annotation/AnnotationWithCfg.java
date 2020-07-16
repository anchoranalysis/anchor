/* (C)2020 */
package org.anchoranalysis.annotation;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectCollection;

public abstract class AnnotationWithCfg implements Annotation {

    public abstract Cfg getCfg();

    protected abstract RegionMap getRegionMap();

    protected abstract int getRegionID();

    public ObjectCollection convertToObjects(ImageDimensions dimensions) {
        return getCfg().calcMask(
                        dimensions,
                        getRegionMap().membershipWithFlagsForIndex(getRegionID()),
                        BinaryValuesByte.getDefault())
                .withoutProperties();
    }
}
