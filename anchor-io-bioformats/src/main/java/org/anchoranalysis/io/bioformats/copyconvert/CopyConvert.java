/* (C)2020 */
package org.anchoranalysis.io.bioformats.copyconvert;

import java.io.IOException;
import java.util.List;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.progress.ProgressReporter;
import org.anchoranalysis.core.progress.ProgressReporterIncrement;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.io.bioformats.DestChnlForIndex;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;

/**
 * Copies the bytes from a {@link IFormatReader} to a list of channels, converting if necessary.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CopyConvert {

    /**
     * Copies all frames, channels, z-slices (in a byte-array) into a destination set of Channels
     * converting them if necessary along the way
     *
     * @param reader the source of the copy
     * @param dest the destination of the copy
     * @param progressReporter
     * @param imageDimensions
     * @param numberChannels
     * @param numberFrames
     * @param bitsPerPixel
     * @param numChnlsPerByteArray
     * @throws FormatException
     * @throws IOException
     */
    public static void copyAllFrames(
            IFormatReader reader,
            List<Channel> dest,
            ProgressReporter progressReporter,
            ImageFileShape targetShape,
            ConvertTo<?> convertTo,
            ReadOptions readOptions)
            throws FormatException, IOException {
        int numChnlsPerByteArray = readOptions.chnlsPerByteArray(reader);

        int numByteArraysPerIteration =
                calcByteArraysPerIter(targetShape.getNumberChannels(), numChnlsPerByteArray);

        try (ProgressReporterIncrement pri = new ProgressReporterIncrement(progressReporter)) {

            pri.setMax(targetShape.totalNumberSlices());
            pri.open();

            IterateOverSlices.iterateDimOrder(
                    reader.getDimensionOrder(),
                    targetShape,
                    numByteArraysPerIteration,
                    (t, z, c, readerIndex) -> {

                        /** Selects a destination channel for a particular relative channel */
                        DestChnlForIndex destC =
                                channelRelative ->
                                        dest.get(
                                                destIndex(
                                                        c + channelRelative,
                                                        t,
                                                        targetShape.getNumberChannels()));

                        byte[] b = reader.openBytes(readerIndex);

                        convertTo.copyAllChnls(
                                targetShape.getImageDimensions(),
                                b,
                                destC,
                                z,
                                numChnlsPerByteArray);

                        pri.update();
                    });
        }
    }

    private static int calcByteArraysPerIter(int numChnl, int numChnlsPerByteArray)
            throws FormatException {

        if ((numChnl % numChnlsPerByteArray) != 0) {
            throw new FormatException(
                    String.format(
                            "numChnls(%d) mod numChnlsPerByteArray(%d) != 0",
                            numChnl, numChnlsPerByteArray));
        }

        return numChnl / numChnlsPerByteArray;
    }

    private static int destIndex(int c, int t, int numChnlsPerFrame) {
        return (t * numChnlsPerFrame) + c;
    }
}
