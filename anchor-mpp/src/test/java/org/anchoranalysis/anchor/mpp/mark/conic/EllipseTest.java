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

import org.anchoranalysis.mpp.mark.conic.Ellipse;
import org.anchoranalysis.spatial.orientation.Orientation2D;
import org.anchoranalysis.spatial.point.Point2d;
import org.anchoranalysis.spatial.point.Point3d;
import org.junit.jupiter.api.Test;

/**
 * Tests {@Ellipse}.
 *
 * @author Owen Feehan
 */
class EllipseTest {

    @Test
    void checkVolume() {
        Ellipse mark = new Ellipse();
        mark.setMarksExplicit(new Point3d(5, 7, 0), new Orientation2D(1.5), new Point2d(5.6, 3.2));

        VolumeTester.assertVolumeMatches(mark);
    }
}
