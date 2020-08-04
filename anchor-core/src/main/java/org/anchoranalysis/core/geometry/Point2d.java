package org.anchoranalysis.core.geometry;

/*
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2016 ETH Zurich, University of Zurich, Owen Feehan
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

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor @EqualsAndHashCode @NoArgsConstructor @Accessors(fluent=true)
public final class Point2d implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    @Getter private double x;
    @Getter private double y;

    public Point2d(Point2d point) {
        this.x = point.x;
        this.y = point.y;
    }

    public void add(Point2i point) {
        this.x = this.x + point.x();
        this.y = this.y + point.y();
    }

    public void scale(double factor) {
        this.x *= factor;
        this.y *= factor;
    }

    public double distanceSquared(Point2d point) {
        double sx = this.x - point.x;
        double sy = this.y - point.y;
        return (sx * sx) + (sy * sy);
    }

    public double distance(Point2d point) {
        return Math.sqrt(distanceSquared(point));
    }

    @Override
    public String toString() {
        return String.format("[%f,%f]", x, y);
    }

    public Point2f toFloat() {
        return new Point2f((float) this.x, (float) this.y);
    }
}
