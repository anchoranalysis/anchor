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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.progress.Progress;
import org.anchoranalysis.core.progress.ProgressIncrement;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.OrientationChange;
import org.anchoranalysis.io.bioformats.DestinationChannelForIndex;
import org.anchoranalysis.io.bioformats.bean.options.ReadOptions;

/**
 * Copies the bytes from a {@link IFormatReader} to a list of channels, converting if necessary.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CopyConvert {

    /**
     * Copies all frames, channels, z-slices (in a byte-array) into a destination set of {@link
     * Channel}s converting them if necessary along the way.
     *
     * @param reader the source of the copy.
     * @param destination the destination of the copy.
     * @param progress tracking progress.
     * @param targetShape the shape of the image-file to convert to (before any orientation
     *     correction).
     * @param readOptions Options that influence how stack is read.
     * @param orientationCorrection any correction of orientation to be applied as bytes are
     *     converted.
     * @throws FormatException
     * @throws IOException
     */
    public static void copyAllFrames(
            IFormatReader reader,
            List<Channel> destination,
            Progress progress,
            ImageFileShape targetShape,
            ConvertTo<?> convertTo,
            ReadOptions readOptions,
            OrientationChange orientationCorrection)
            throws FormatException, IOException {
        int numberChannelsPerByteArray = readOptions.channelsPerByteArray(reader);

        int numberByteArraysPerIteration =
                calculateByteArraysPerIteration(
                        targetShape.getNumberChannels(), numberChannelsPerByteArray);

        try (ProgressIncrement progressIncrement = new ProgressIncrement(progress)) {

            progressIncrement.setMax(targetShape.totalNumberSlices());
            progressIncrement.open();

            IterateOverSlices.iterateDimensionsOrder(
                    reader.getDimensionOrder(),
                    targetShape,
                    numberByteArraysPerIteration,
                    (t, z, c, readerIndex) -> {

                        /** Selects a destination channel for a particular relative channel */
                        DestinationChannelForIndex destinationChannel =
                                channelIndexRelative ->
                                        destination.get(
                                                destinationIndex(
                                                        c + channelIndexRelative,
                                                        t,
                                                        targetShape.getNumberChannels()));

                        convertTo.copyAllChannels(
                                targetShape.getImageDimensions(),
                                openByteBuffer(reader, readerIndex),
                                destinationChannel,
                                z,
                                numberChannelsPerByteArray,
                                orientationCorrection);

                        progressIncrement.update();
                    });
        }
    }

    /** Reads bytes from a {@link IFormatReader} making sure the endianness is correct. */
    private static ByteBuffer openByteBuffer(IFormatReader reader, int index)
            throws FormatException, IOException {
        byte[] bufferArray = reader.openBytes(index);
        ByteBuffer buffer = ByteBuffer.wrap(bufferArray);
        buffer.order(reader.isLittleEndian() ? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);
        return buffer;
    }

    private static int calculateByteArraysPerIteration(
            int numberChannels, int numberChannelsPerByteArray) throws FormatException {

        if ((numberChannels % numberChannelsPerByteArray) != 0) {
            throw new FormatException(
                    String.format(
                            "numberChannels(%d) mod numberChannelsPerByteArray(%d) != 0",
                            numberChannels, numberChannelsPerByteArray));
        }

        return numberChannels / numberChannelsPerByteArray;
    }

    private static int destinationIndex(
            int channelIndex, int timeIndex, int numberChannelsPerFrame) {
        return (timeIndex * numberChannelsPerFrame) + channelIndex;
    }
}
