/* (C)2020 */
package org.anchoranalysis.image.bean.nonbean.arrangeraster;

import java.util.Iterator;
import org.anchoranalysis.image.stack.rgb.RGBStack;

public interface ArrangeRaster {

    BBoxSetOnPlane createBBoxSetOnPlane(Iterator<RGBStack> rasterIterator)
            throws ArrangeRasterException;
}
