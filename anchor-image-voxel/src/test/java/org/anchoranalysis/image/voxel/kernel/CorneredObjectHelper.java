/*-
 * #%L
 * anchor-image-voxel
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
package org.anchoranalysis.image.voxel.kernel;

import lombok.NoArgsConstructor;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.ObjectMaskFixture;
import org.anchoranalysis.spatial.point.Point3i;

@NoArgsConstructor
class CorneredObjectHelper {

    /**
     * A corner for the object that doesn't touch a boundary, assuming it exists in a
     * sufficiently-large scene.
     */
    private static final Point3i CORNER_NOT_AT_BORDER = new Point3i(2, 3, 2);

    /** A corner for the object that sits at the origin. */
    private static final Point3i CORNER_ORIGIN = new Point3i(0, 0, 0);

    public static ObjectMask createObjectFromFixture(
            ObjectMaskFixture fixture, boolean scene3D, boolean atOrigin) {
        return fixture.filledMask(corner(scene3D, atOrigin));
    }

    private static Point3i corner(boolean scene3D, boolean atOrigin) {
        if (atOrigin) {
            return CORNER_ORIGIN;
        } else {
            return FlattenHelper.maybeFlattenPoint(CORNER_NOT_AT_BORDER, scene3D);
        }
    }
}
