/* (C)2020 */
package org.anchoranalysis.io.bioformats.bean.options;

import java.util.List;
import java.util.Optional;
import loci.formats.IFormatReader;
import org.anchoranalysis.bean.AnchorBean;

public abstract class ReadOptions extends AnchorBean<ReadOptions> {

    public abstract int sizeC(IFormatReader reader);

    public abstract int sizeT(IFormatReader reader);

    public abstract int sizeZ(IFormatReader reader);

    public abstract int effectiveBitsPerPixel(IFormatReader reader);

    public abstract int chnlsPerByteArray(IFormatReader reader);

    /** Is it an image with three channels (red, green and blue)? */
    public abstract boolean isRGB(IFormatReader reader);

    /** Returns a list of channel-names or NULL if they are unavailable */
    public abstract Optional<List<String>> determineChannelNames(IFormatReader reader);
}
