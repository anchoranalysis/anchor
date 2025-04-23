/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.anchor.mpp.mark.conic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.mpp.mark.MarkToObjectConverter;

/**
 * Checks that the volume of an object is similar to the number of voxels in an object
 * representation.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class VolumeTester {

    private static final double RATIO_TOLERANCE_VOLUME = 0.02;

    private static final MarkToObjectConverter CONVERTER =
            new MarkToObjectConverter(new Dimensions(30, 40, 50));

    /**
     * Establishes that the number of voxels in the object-representation of a mark, is almost
     * identical to it's calculated volume.
     *
     * @param mark the mark to check.
     */
    public static void assertVolumeMatches(Mark mark) {
        assertEqualsRatioTolerance(numberVoxelsFromObject(mark), mark.volume(0));
    }

    /** Allows a tolerance, as a % of the expected value. */
    private static void assertEqualsRatioTolerance(double expected, double actual) {
        assertEquals(expected, actual, expected * RATIO_TOLERANCE_VOLUME);
    }

    /** Gets the number of voxels from an object-representation of a {@link Mark}. */
    private static int numberVoxelsFromObject(Mark mark) {
        return CONVERTER.convert(mark).numberVoxelsOn();
    }
}
