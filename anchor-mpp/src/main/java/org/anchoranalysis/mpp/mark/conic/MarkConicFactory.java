/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.mark.conic;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.orientation.Orientation2D;
import org.anchoranalysis.image.core.orientation.Orientation3DEulerAngles;
import org.anchoranalysis.mpp.mark.Mark;
import org.anchoranalysis.spatial.point.Point2d;
import org.anchoranalysis.spatial.point.Point3d;
import org.anchoranalysis.spatial.point.Point3i;
import org.anchoranalysis.spatial.point.PointConverter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarkConicFactory {

    public static Mark createMarkFromPoint(Point3i point, int size, boolean do3D) {
        return createMarkFromPoint(PointConverter.doubleFromInt(point), size, do3D);
    }

    public static Mark createMarkFromPoint(Point3d point, int size, boolean do3D) {
        Preconditions.checkArgument(size > 0);
        Preconditions.checkArgument(do3D || point.z() == 0);

        if (do3D) {
            Ellipsoid me = new Ellipsoid();
            me.setMarksExplicit(
                    point, new Orientation3DEulerAngles(), new Point3d(size, size, size));
            return me;
        } else {
            Ellipse me = new Ellipse();
            me.setMarksExplicit(point, new Orientation2D(), new Point2d(size, size));
            return me;
        }
    }
}
