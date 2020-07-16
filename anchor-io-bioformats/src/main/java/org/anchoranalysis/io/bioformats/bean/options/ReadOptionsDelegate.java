/* (C)2020 */
package org.anchoranalysis.io.bioformats.bean.options;

import java.util.List;
import java.util.Optional;
import loci.formats.IFormatReader;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

/**
 * Forces a particular settings, but otherwise uses settings from a delegate
 *
 * <p>Sub-classes of this deliberately break the Liskov substitution principle by replacing existing
 * behaviour
 *
 * @author Owen Feehan
 */
public abstract class ReadOptionsDelegate extends ReadOptions {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ReadOptions options = new Default();
    // END BEAN PROPERTIES

    @Override
    public Optional<List<String>> determineChannelNames(IFormatReader reader) {
        return options.determineChannelNames(reader);
    }

    @Override
    public int sizeT(IFormatReader reader) {
        return options.sizeT(reader);
    }

    @Override
    public int sizeZ(IFormatReader reader) {
        return options.sizeZ(reader);
    }

    @Override
    public int sizeC(IFormatReader reader) {
        return options.sizeC(reader);
    }

    @Override
    public boolean isRGB(IFormatReader reader) {
        return options.isRGB(reader);
    }

    @Override
    public int effectiveBitsPerPixel(IFormatReader reader) {
        return options.effectiveBitsPerPixel(reader);
    }

    @Override
    public int chnlsPerByteArray(IFormatReader reader) {
        return options.chnlsPerByteArray(reader);
    }

    protected ReadOptions delegate() {
        return options;
    }
}
