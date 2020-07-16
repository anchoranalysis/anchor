/* (C)2020 */
package org.anchoranalysis.io.bioformats.bean.options;

import loci.formats.IFormatReader;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Positive;

public class ForceBitsPerPixel extends ReadOptionsDelegate {

    // START BEAN PROPERTIES
    @BeanField @Positive private int bitsPerPixel = 0;
    // END BEAN PROPERTIES

    @Override
    public boolean isRGB(IFormatReader reader) {
        if (bitsPerPixel == 8) {
            return delegate().isRGB(reader);
        } else {
            return false;
        }
    }

    public int getBitsPerPixel() {
        return bitsPerPixel;
    }

    public void setBitsPerPixel(int bitsPerPixel) {
        this.bitsPerPixel = bitsPerPixel;
    }
}
