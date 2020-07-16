/* (C)2020 */
package org.anchoranalysis.io.bioformats.bean.options;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import loci.formats.IFormatReader;

public class Default extends ReadOptions {

    // START BEAN FIELDS
    // END BEAN FIELDS

    @Override
    public int sizeT(IFormatReader reader) {
        return reader.getSizeT();
    }

    @Override
    public int sizeZ(IFormatReader reader) {
        return reader.getSizeZ();
    }

    @Override
    public int sizeC(IFormatReader reader) {
        return reader.getSizeC();
    }

    @Override
    public boolean isRGB(IFormatReader reader) {
        return reader.isRGB();
    }

    @Override
    public int effectiveBitsPerPixel(IFormatReader reader) {
        Object bitDepth = reader.getMetadataValue("Acquisition Bit Depth");
        if (bitDepth != null) {
            return Integer.valueOf((String) bitDepth);
        } else {
            return reader.getBitsPerPixel();
        }
    }

    @Override
    public int chnlsPerByteArray(IFormatReader reader) {
        return reader.getRGBChannelCount();
    }

    @Override
    public Optional<List<String>> determineChannelNames(IFormatReader reader) {

        String formatName = reader.getFormat();
        if (formatName.equals("Zeiss CZI")) {
            Optional<List<String>> names =
                    determineChannelNamesWithPrefix(
                            reader, "Metadata DisplaySetting Channels Channel ShortName ");

            // We try again
            if (!names.isPresent()) {
                names =
                        determineChannelNamesWithPrefix(
                                reader,
                                "Metadata Experiment ExperimentBlocks AcquisitionBlock MultiTrackSetup Track Channels Channel FluorescenceDye ShortName ");
            }

            return names;
        } else if (formatName.equals("Zeiss Vision Image (ZVI)")) {
            return determineChannelNamesWithPrefix(reader, "Channel Name ");
        }
        return Optional.empty();
    }

    private static Optional<List<String>> determineChannelNamesWithPrefix(
            IFormatReader reader, String prefixString) {

        int numChnl = reader.getSizeC();

        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < numChnl; i++) {
            Object o = reader.getMetadataValue(prefixString + i);
            if (o == null) {
                return Optional.empty();
            }
            if (!(o instanceof String)) {
                return Optional.empty();
            }
            names.add((String) o);
        }
        return Optional.of(names);
    }
}
