/* (C)2020 */
package org.anchoranalysis.io.ij.bean.writer;

import ij.io.FileSaver;
import org.anchoranalysis.image.io.RasterIOException;

public class IJPngWriter extends IJWriterNoStack {

    @Override
    protected boolean writeRaster(FileSaver fs, String outPath, boolean asStack)
            throws RasterIOException {

        if (asStack) {
            throw new RasterIOException("Writing as stack unsupported for this format");
        } else {
            return fs.saveAsPng(outPath);
        }
    }

    @Override
    public String dfltExt() {
        return "png";
    }
}
