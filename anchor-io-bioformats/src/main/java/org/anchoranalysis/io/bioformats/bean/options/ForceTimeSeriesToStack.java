/* (C)2020 */
package org.anchoranalysis.io.bioformats.bean.options;

import loci.formats.IFormatReader;

/** Treats a time-series as if it was a z-stack */
public class ForceTimeSeriesToStack extends ReadOptionsDelegate {

    @Override
    public int sizeT(IFormatReader reader) {
        // Block normal time-series
        return 1;
    }

    @Override
    public int sizeZ(IFormatReader reader) {
        return reader.getSizeT() * delegate().sizeZ(reader);
    }

    @Override
    public boolean isRGB(IFormatReader reader) {
        return delegate().isRGB(reader);
    }
}
