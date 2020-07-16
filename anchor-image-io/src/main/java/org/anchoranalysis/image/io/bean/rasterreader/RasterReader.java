/* (C)2020 */
package org.anchoranalysis.image.io.bean.rasterreader;

import java.nio.file.Path;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;

public abstract class RasterReader extends AnchorBean<RasterReader> {

    public abstract OpenedRaster openFile(Path filepath) throws RasterIOException;
}
