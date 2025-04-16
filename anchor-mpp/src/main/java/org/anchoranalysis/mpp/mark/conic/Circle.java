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

import lombok.NoArgsConstructor;
import org.anchoranalysis.mpp.bean.bound.Bound;
import org.anchoranalysis.mpp.mark.Mark;

/**
 * Represents a 2D circle mark.
 *
 * <p>This class extends {@link MarkWithPositionAndSingleRadius} to provide functionality
 * specific to circular marks.</p>
 */
@NoArgsConstructor
public class Circle extends MarkWithPositionAndSingleRadius {

    private static final long serialVersionUID = 8551900716243748046L;

    /**
     * Creates a circle with a radius within particular bounds.
     *
     * @param boundRadius the bound for the circle's radius
     */
    public Circle(Bound boundRadius) {
        super(boundRadius);
    }

    /**
     * Copy constructor.
     *
     * @param source the Circle to copy from
     */
    public Circle(Circle source) {
        super(source);
    }

    @Override
    public String getName() {
        return "circle";
    }

    @Override
    public double volume(int regionID) {
        return (2 * Math.PI * radiusForRegionSquared(regionID));
    }

    @Override
    public String toString() {
        return String.format("%s %s pos=%s %s", "circle", identifier(), strPos(), strMarks());
    }

    // The duplicate operation for the marks (we avoid clone() in case its confusing, we might not
    // always shallow copy)
    @Override
    public Mark duplicate() {
        return new Circle(this);
    }

    @Override
    public int numberDimensions() {
        return 2;
    }
}