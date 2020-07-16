/*-
 * #%L
 * anchor-image-io
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
/* (C)2020 */
package org.anchoranalysis.image.io.bean.rasterreader;

import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.stack.Stack;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RasterReaderUtilities {

    public static Stack openStackFromPath(RasterReader rasterReader, Path path)
            throws RasterIOException {
        OpenedRaster openedRaster = rasterReader.openFile(path);

        try {
            if (openedRaster.numSeries() != 1) {
                throw new RasterIOException("there must be exactly one series");
            }

            Stack stack = openedRaster.open(0, ProgressReporterNull.get()).get(0);
            return stack.duplicate();
        } finally {
            openedRaster.close();
        }
    }

    public static Mask openBinaryChnl(RasterReader rasterReader, Path path, BinaryValues bv)
            throws RasterIOException {

        Stack stack = openStackFromPath(rasterReader, path);

        if (stack.getNumChnl() != 1) {
            throw new RasterIOException(
                    String.format(
                            "There must be exactly one channel, but there are %d",
                            stack.getNumChnl()));
        }

        return new Mask(stack.getChnl(0), bv);
    }
}
