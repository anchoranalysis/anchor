/* (C)2020 */
package org.anchoranalysis.io.bioformats.bean.options;

import loci.formats.IFormatReader;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Positive;

public class ForceChannelsPerByteArray extends ReadOptionsDelegate {

    // START BEAN PROPERTIES
    @BeanField @Positive private int channelsPerByteArray;
    // END BEAN PROPERTIES

    // Overrides with constant
    @Override
    public int chnlsPerByteArray(IFormatReader reader) {
        return channelsPerByteArray;
    }

    public int getChannelsPerByteArray() {
        return channelsPerByteArray;
    }

    public void setChannelsPerByteArray(int channelsPerByteArray) {
        this.channelsPerByteArray = channelsPerByteArray;
    }
}
