/* (C)2020 */
package org.anchoranalysis.image.bean.arrangeraster;

import java.util.Iterator;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.ArrangeRasterException;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.BBoxSetOnPlane;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public class SingleRaster extends ArrangeRasterBean {

    @Override
    public BBoxSetOnPlane createBBoxSetOnPlane(Iterator<RGBStack> rasterIterator)
            throws ArrangeRasterException {

        if (!rasterIterator.hasNext()) {
            throw new ArrangeRasterException("iterator has no more rasters");
        }

        RGBStack stack = rasterIterator.next();

        Extent extent = stack.getChnl(0).getDimensions().getExtent();

        return new BBoxSetOnPlane(extent, new BoundingBox(extent));
    }
}
