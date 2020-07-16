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
/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark.points;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.function.Function;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3f;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.PointConverter;

public class MarkPointListFactory {

    private MarkPointListFactory() {}

    public static MarkPointList create(List<Point3d> points) {
        return create(points, -1);
    }

    public static MarkPointList create(List<Point3d> points, int id) {
        MarkPointList mark = new MarkPointList();
        mark.getPoints().addAll(points);
        mark.setId(id);
        mark.updateAfterPointsChange();
        return mark;
    }

    public static MarkPointList createMarkFromPoints3f(List<Point3f> points) {
        return createMarkFromPoints(points, PointConverter::doubleFromFloat);
    }

    public static MarkPointList createMarkFromPoints3i(List<Point3i> points) {
        return createMarkFromPoints(points, PointConverter::doubleFromInt);
    }

    private static <T> MarkPointList createMarkFromPoints(
            List<T> points, Function<T, Point3d> convert) {
        Preconditions.checkArgument(!points.isEmpty(), "are empty");

        return new MarkPointList(points.stream().map(convert));
    }
}
