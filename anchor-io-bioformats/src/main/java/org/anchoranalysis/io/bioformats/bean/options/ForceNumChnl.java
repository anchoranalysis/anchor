/* (C)2020 */
package org.anchoranalysis.io.bioformats.bean.options;

import loci.formats.IFormatReader;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Positive;

public class ForceNumChnl extends ReadOptionsDelegate {

    // START BEAN PROPERTIES
    @BeanField @Positive private int numChnl;
    // END BEAN PROPERTIES

    @Override
    public int sizeC(IFormatReader reader) {
        return numChnl;
    }

    @Override
    public boolean isRGB(IFormatReader reader) {
        // Not supported when the numer of channels is being forced
        return false;
    }

    public int getNumChnl() {
        return numChnl;
    }

    public void setNumChnl(int numChnl) {
        this.numChnl = numChnl;
    }
}
