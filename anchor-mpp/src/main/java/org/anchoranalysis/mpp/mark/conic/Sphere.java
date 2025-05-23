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
 * Represents a 3D sphere mark.
 *
 * <p>This class extends {@link MarkWithPositionAndSingleRadius} to provide functionality specific
 * to spherical marks in 3D space.
 */
@NoArgsConstructor
public class Sphere extends MarkWithPositionAndSingleRadius {

    private static final long serialVersionUID = -3526056946146656810L;

    /**
     * Creates a sphere with a radius within particular bounds.
     *
     * @param boundRadius the bound for the sphere's radius
     */
    public Sphere(Bound boundRadius) {
        super(boundRadius);
    }

    /**
     * Copy constructor.
     *
     * @param source the Sphere to copy from
     */
    public Sphere(Sphere source) {
        super(source);
    }

    @Override
    public String getName() {
        return "sphere";
    }

    @Override
    public double volume(int regionID) {
        double radiusCubed = Math.pow(radiusForRegion(regionID), 3.0);
        return (4 * Math.PI * radiusCubed) / 3;
    }

    @Override
    public String toString() {
        return String.format("%s %s pos=%s %s", "Sphr", identifier(), positionString(), strMarks());
    }

    // The duplicate operation for the marks (we avoid clone() in case its confusing, we might not
    // always shallow copy)
    @Override
    public Mark duplicate() {
        return new Sphere(this);
    }

    @Override
    public int numberDimensions() {
        return 3;
    }
}
