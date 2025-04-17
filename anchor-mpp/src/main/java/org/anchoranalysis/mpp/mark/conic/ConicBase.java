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

import java.util.Arrays;
import java.util.Optional;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.mpp.mark.MarkWithPosition;
import org.anchoranalysis.spatial.orientation.Orientation;
import org.anchoranalysis.spatial.point.Point3d;

/**
 * Abstract base class for conic marks (e.g., ellipsoids, spheres).
 *
 * <p>This class extends {@link MarkWithPosition} to provide common functionality for conic-shaped
 * marks in 3D space.
 */
public abstract class ConicBase extends MarkWithPosition {

    private static final long serialVersionUID = 1680124471263339009L;

    /** Default constructor. */
    protected ConicBase() {
        super();
    }

    /**
     * Constructor that copies from another MarkWithPosition.
     *
     * @param source the source MarkWithPosition to copy from
     */
    protected ConicBase(MarkWithPosition source) {
        super(source);
    }

    /**
     * Creates an array of radii resolved to the given resolution.
     *
     * @param resolution an optional resolution to consider
     * @return an array of resolved radii
     */
    public abstract double[] createRadiiArrayResolved(Optional<Resolution> resolution);

    /**
     * Creates an array of radii in their original units.
     *
     * @return an array of radii
     */
    public abstract double[] createRadiiArray();

    /**
     * Sets the mark's properties explicitly.
     *
     * @param pos the position of the mark
     * @param orientation the orientation of the mark
     * @param radii the radii of the mark
     */
    public abstract void setMarksExplicit(Point3d pos, Orientation orientation, Point3d radii);

    /**
     * Sets the mark's position and orientation explicitly.
     *
     * @param position the position of the mark
     * @param orientation the orientation of the mark
     */
    public abstract void setMarksExplicit(Point3d position, Orientation orientation);

    /**
     * Sets the mark's position explicitly.
     *
     * @param position the position of the mark
     */
    public abstract void setMarksExplicit(Point3d position);

    /**
     * Returns an ordered array of radii resolved to the given resolution.
     *
     * @param resolution an optional resolution to consider
     * @return an ordered array of resolved radii
     */
    public double[] radiiOrderedResolved(Optional<Resolution> resolution) {
        double[] radii = createRadiiArrayResolved(resolution);
        Arrays.sort(radii);
        return radii;
    }

    /**
     * Returns an ordered array of radii in their original units.
     *
     * @return an ordered array of radii
     */
    public double[] radiiOrdered() {
        double[] radii = createRadiiArray();
        Arrays.sort(radii);
        return radii;
    }
}
