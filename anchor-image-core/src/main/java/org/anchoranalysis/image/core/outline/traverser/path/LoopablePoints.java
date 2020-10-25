/*-
 * #%L
 * anchor-image
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

package org.anchoranalysis.image.core.outline.traverser.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.anchoranalysis.spatial.point.Point3i;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * A set of points that can be looped, so as to form a path that ends with the same point that it
 * begins with.... without breaking continuity.
 *
 * @author feehano
 */
@AllArgsConstructor @Accessors(fluent=true)
public class LoopablePoints {

    // This is a point that previously connected with the loop, but which we repeat
    //   after we do a loop, so that the end point is identical to the end of the previous
    //   points
    @Getter private List<Point3i> points;
    private Point3i connectingPoint;

    public List<Point3i> loopPointsLeft() {
        List<Point3i> out = new ArrayList<>();

        out.add(connectingPoint);

        if (points.size() == 1) {
            // Special behaviour for a single-point as it doesn't need reversing
            out.addAll(points);
        } else {
            out.addAll(reversedList(points));
            out.remove(out.size() - 1); // Remove last item to prevent duplication
            out.addAll(points);
        }

        return out;
    }

    public List<Point3i> loopPointsRight() {
        List<Point3i> out = new ArrayList<>(points);

        if (points.size() == 1) {
            // Special behaviour for a single-point as it doesn't need reversing
        } else {
            out.remove(out.size() - 1); // Remove last item to prevent duplication
            out.addAll(reversedList(points));
        }

        out.add(connectingPoint);
        return out;
    }

    public int size() {
        return points.size();
    }

    @Override
    public String toString() {
        return points.toString();
    }
    
    private static List<Point3i> reversedList(List<Point3i> list) {
        List<Point3i> copy = new ArrayList<>(list);
        Collections.reverse(copy);
        return copy;
    }
}
