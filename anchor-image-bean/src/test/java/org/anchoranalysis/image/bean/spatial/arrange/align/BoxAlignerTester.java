/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.bean.spatial.arrange.align;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Helps test a {@link BoxAligner}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class BoxAlignerTester {

    /** The smaller bounding-box passed to {@link BoxAligner#align}. */
    public static final BoundingBox SMALLER =
            BoundingBox.createReuse(new Point3i(20, 30, 0), new Extent(10, 20, 30));

    /** The larger bounding-box passed to {@link BoxAligner#align}. */
    public static final BoundingBox LARGER =
            BoundingBox.createReuse(new Point3i(5, 10, 15), new Extent(120, 200, 30));

    /**
     * Calls {@link BoxAligner#align} with the smaller and larger bounding box, and checks the
     * expected result.
     *
     * @param aligner the {@link BoxAligner} to use.
     * @param expectedCorner the expected corner on the returned {@link BoundingBox}.
     * @param expectedExtent the expected extent on the returned {@link BoundingBox}.
     * @throws OperationFailedException if thrown by {@link BoxAligner}.
     */
    public static void doTest(
            BoxAligner aligner, ReadableTuple3i expectedCorner, Extent expectedExtent)
            throws OperationFailedException {
        // We use null here as a hack, as the parameter shouldn't ever be needed
        try {
            aligner.checkMisconfigured(null);
        } catch (BeanMisconfiguredException e) {
            throw new OperationFailedException(e);
        }
        BoundingBox box = aligner.align(SMALLER, LARGER);
        assertEquals(expectedCorner, box.cornerMin(), "cornerMin");
        assertEquals(expectedExtent, box.extent(), "extent");
    }
}
