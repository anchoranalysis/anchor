/* (C)2020 */
package org.anchoranalysis.io.bioformats.bean.options;

import loci.formats.IFormatReader;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Positive;

public class ForceNumFrame extends ReadOptionsDelegate {

    // START BEAN PROPERTIES
    @BeanField @Positive private int numFrame;
    // END BEAN PROPERTIES

    @Override
    public int sizeT(IFormatReader reader) {
        return numFrame;
    }

    public int getNumFrame() {
        return numFrame;
    }

    public void setNumFrame(int numFrame) {
        this.numFrame = numFrame;
    }
}
