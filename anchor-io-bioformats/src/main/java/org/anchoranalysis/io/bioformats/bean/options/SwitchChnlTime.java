/* (C)2020 */
package org.anchoranalysis.io.bioformats.bean.options;

import java.util.List;
import java.util.Optional;
import loci.formats.IFormatReader;

/** Treats the channel information as if it's time, and vice versa */
public class SwitchChnlTime extends ReadOptionsDelegate {

    @Override
    public Optional<List<String>> determineChannelNames(IFormatReader reader) {
        // Always return null, as we use the time-series instead
        return Optional.empty();
    }

    @Override
    public boolean isRGB(IFormatReader reader) {
        // Not supported when switching
        return false;
    }
}
