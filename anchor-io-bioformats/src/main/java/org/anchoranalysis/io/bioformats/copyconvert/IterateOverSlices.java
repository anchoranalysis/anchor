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
    public interface ApplyIterationToChnl {
        void apply(int t, int z, int c, int chnlIndex) throws IOException, FormatException;
    }

    /**
     * Iterates through all the frames, channels, z-slices in whatever order the reader recommends.
     *
     * @param dimOrder
     * @param shape the shape of the dimensions of the data
     * @param numChnlsPerByteArray
     * @param chnlIteration called for each unique z-slice from each channel and each frame
     * @throws IOException
     * @throws FormatException
     */
    public static void iterateDimOrder(
            String dimOrder,
            ImageFileShape shape,
            int numByteArrays,
            ApplyIterationToChnl chnlIteration)
            throws IOException, FormatException {

        if (dimOrder.equalsIgnoreCase("XYCZT")) {
            applyXYCZT(shape, numByteArrays, chnlIteration);

        } else if (dimOrder.equalsIgnoreCase("XYZCT")) {
            applyXYZCT(shape, numByteArrays, chnlIteration);

        } else if (dimOrder.equalsIgnoreCase("XYZTC")) {
            applyXYZTC(shape, numByteArrays, chnlIteration);

        } else if (dimOrder.equalsIgnoreCase("XYCTZ")) {
            applyXYCTZ(shape, numByteArrays, chnlIteration);

        } else if (dimOrder.equalsIgnoreCase("XYTCZ")) {
            applyXYTCZ(shape, numByteArrays, chnlIteration);
        } else {
            throw new IOException(String.format("dimOrder '%s' not supported", dimOrder));
        }
    }

    private static void applyXYCZT(
            ImageFileShape shape, int numByteArrays, ApplyIterationToChnl chnlIteration)
            throws IOException, FormatException {
        int chnlIndex = 0;
        for (int t = 0; t < shape.getNumberFrames(); t++) {
            for (int z = 0; z < shape.getNumberSlices(); z++) {
                for (int c = 0; c < numByteArrays; c++) {
                    chnlIteration.apply(t, z, c, chnlIndex++);
                }
            }
        }
    }

    private static void applyXYZCT(
            ImageFileShape shape, int numByteArrays, ApplyIterationToChnl chnlIteration)
            throws IOException, FormatException {
        int chnlIndex = 0;
        for (int t = 0; t < shape.getNumberFrames(); t++) {
            for (int c = 0; c < numByteArrays; c++) {
                for (int z = 0; z < shape.getNumberSlices(); z++) {
                    chnlIteration.apply(t, z, c, chnlIndex++);
                }
            }
        }
    }

    private static void applyXYZTC(
            ImageFileShape targetShape, int numByteArrays, ApplyIterationToChnl chnlIteration)
            throws IOException, FormatException {
        int chnlIndex = 0;
        for (int c = 0; c < numByteArrays; c++) {
            for (int t = 0; t < targetShape.getNumberFrames(); t++) {
                for (int z = 0; z < targetShape.getNumberSlices(); z++) {
                    chnlIteration.apply(t, z, c, chnlIndex++);
                }
            }
        }
    }

    private static void applyXYCTZ(
            ImageFileShape shape, int numByteArrays, ApplyIterationToChnl chnlIteration)
            throws IOException, FormatException {
        int chnlIndex = 0;
        for (int z = 0; z < shape.getNumberSlices(); z++) {
            for (int t = 0; t < shape.getNumberFrames(); t++) {
                for (int c = 0; c < numByteArrays; c++) {
                    chnlIteration.apply(t, z, c, chnlIndex++);
                }
            }
        }
    }

    private static void applyXYTCZ(
            ImageFileShape shape, int numByteArrays, ApplyIterationToChnl chnlIteration)
            throws IOException, FormatException {
        int chnlIndex = 0;
        for (int z = 0; z < shape.getNumberSlices(); z++) {
            for (int c = 0; c < numByteArrays; c++) {
                for (int t = 0; t < shape.getNumberFrames(); t++) {
                    chnlIteration.apply(t, z, c, chnlIndex++);
                }
            }
        }
    }
}
