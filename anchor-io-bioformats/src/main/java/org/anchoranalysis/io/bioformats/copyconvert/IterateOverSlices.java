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
import loci.formats.FormatException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Different ways of iterating through the different slices */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class IterateOverSlices {

    @FunctionalInterface
    public interface ApplyIterationToChannel {
        void apply(int t, int z, int c, int channelIndex) throws IOException, FormatException;
    }

    /**
     * Iterates through all the frames, channels, z-slices in whatever order the reader recommends.
     *
     * @param dimensionsOrder
     * @param shape the shape of the dimensions of the data
     * @param channelIteration called for each unique z-slice from each channel and each frame
     * @throws IOException
     * @throws FormatException
     */
    public static void iterateDimensionsOrder(
            String dimensionsOrder,
            ImageFileShape shape,
            int numberByteArrays,
            ApplyIterationToChannel channelIteration)
            throws IOException, FormatException {

        if (dimensionsOrder.equalsIgnoreCase("XYCZT")) {
            applyXYCZT(shape, numberByteArrays, channelIteration);

        } else if (dimensionsOrder.equalsIgnoreCase("XYZCT")) {
            applyXYZCT(shape, numberByteArrays, channelIteration);

        } else if (dimensionsOrder.equalsIgnoreCase("XYZTC")) {
            applyXYZTC(shape, numberByteArrays, channelIteration);

        } else if (dimensionsOrder.equalsIgnoreCase("XYCTZ")) {
            applyXYCTZ(shape, numberByteArrays, channelIteration);

        } else if (dimensionsOrder.equalsIgnoreCase("XYTCZ")) {
            applyXYTCZ(shape, numberByteArrays, channelIteration);
        } else {
            throw new IOException(String.format("dimensionsOrder '%s' not supported", dimensionsOrder));
        }
    }

    private static void applyXYCZT(
            ImageFileShape shape, int numberByteArrays, ApplyIterationToChannel channelIteration)
            throws IOException, FormatException {
        int channelIndex = 0;
        for (int t = 0; t < shape.getNumberFrames(); t++) {
            for (int z = 0; z < shape.getNumberSlices(); z++) {
                for (int c = 0; c < numberByteArrays; c++) {
                    channelIteration.apply(t, z, c, channelIndex++);
                }
            }
        }
    }

    private static void applyXYZCT(
            ImageFileShape shape, int numberByteArrays, ApplyIterationToChannel channelIteration)
            throws IOException, FormatException {
        int channelIndex = 0;
        for (int t = 0; t < shape.getNumberFrames(); t++) {
            for (int c = 0; c < numberByteArrays; c++) {
                for (int z = 0; z < shape.getNumberSlices(); z++) {
                    channelIteration.apply(t, z, c, channelIndex++);
                }
            }
        }
    }

    private static void applyXYZTC(
            ImageFileShape targetShape, int numberByteArrays, ApplyIterationToChannel channelIteration)
            throws IOException, FormatException {
        int channelIndex = 0;
        for (int c = 0; c < numberByteArrays; c++) {
            for (int t = 0; t < targetShape.getNumberFrames(); t++) {
                for (int z = 0; z < targetShape.getNumberSlices(); z++) {
                    channelIteration.apply(t, z, c, channelIndex++);
                }
            }
        }
    }

    private static void applyXYCTZ(
            ImageFileShape shape, int numberByteArrays, ApplyIterationToChannel channelIteration)
            throws IOException, FormatException {
        int channelIndex = 0;
        for (int z = 0; z < shape.getNumberSlices(); z++) {
            for (int t = 0; t < shape.getNumberFrames(); t++) {
                for (int c = 0; c < numberByteArrays; c++) {
                    channelIteration.apply(t, z, c, channelIndex++);
                }
            }
        }
    }

    private static void applyXYTCZ(
            ImageFileShape shape, int numberByteArrays, ApplyIterationToChannel channelIteration)
            throws IOException, FormatException {
        int channelIndex = 0;
        for (int z = 0; z < shape.getNumberSlices(); z++) {
            for (int c = 0; c < numberByteArrays; c++) {
                for (int t = 0; t < shape.getNumberFrames(); t++) {
                    channelIteration.apply(t, z, c, channelIndex++);
                }
            }
        }
    }
}
