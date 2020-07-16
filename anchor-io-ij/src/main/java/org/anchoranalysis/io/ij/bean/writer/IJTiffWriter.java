/* (C)2020 */
package org.anchoranalysis.io.ij.bean.writer;

import ij.io.FileSaver;

public class IJTiffWriter extends IJWriterSupportsStack {

    @Override
    protected boolean writeRaster(FileSaver fs, String outPath, boolean asStack) {
        if (asStack) {
            return fs.saveAsTiffStack(outPath);
        } else {
            return fs.saveAsTiff(outPath);
        }
    }

    @Override
    public String dfltExt() {
        return "tif";
    }
}
