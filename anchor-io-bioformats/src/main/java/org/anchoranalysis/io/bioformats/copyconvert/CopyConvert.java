/*-
 * #%L
 * anchor-io-bioformats
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
        int numberChannelsPerByteArray = readOptions.chnlsPerByteArray(reader);

        int numberByteArraysPerIteration =
                calculateByteArraysPerIteration(targetShape.getNumberChannels(), numberChannelsPerByteArray);

        try (ProgressReporterIncrement pri = new ProgressReporterIncrement(progressReporter)) {

            pri.setMax(targetShape.totalNumberSlices());
            pri.open();

            IterateOverSlices.iterateDimOrder(
                    reader.getDimensionOrder(),
                    targetShape,
                    numberByteArraysPerIteration,
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
                                numberChannelsPerByteArray);

                        pri.update();
                    });
        }
    }

    private static int calculateByteArraysPerIteration(int numChnl, int numChnlsPerByteArray)
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
