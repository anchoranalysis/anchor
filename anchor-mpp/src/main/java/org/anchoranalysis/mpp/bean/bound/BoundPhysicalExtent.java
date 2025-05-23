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

package org.anchoranalysis.mpp.bean.bound;

import java.util.Optional;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.core.dimensions.Resolution;

/**
 * A bound representing physical extents, which can be resolved based on image resolution.
 *
 * <p>This class extends {@link BoundMinMax} to provide functionality specific to physical extents,
 * where the bound values may be adjusted based on the image resolution.
 */
@NoArgsConstructor
public class BoundPhysicalExtent extends BoundMinMax {

    private static final long serialVersionUID = -5440824428055546445L;

    /**
     * Creates a new instance with specified minimum and maximum values.
     *
     * @param min the minimum physical extent
     * @param max the maximum physical extent
     */
    public BoundPhysicalExtent(double min, double max) {
        super(min, max);
    }

    /**
     * Creates a new instance by copying another BoundPhysicalExtent.
     *
     * @param source the source BoundPhysicalExtent to copy from
     */
    public BoundPhysicalExtent(BoundPhysicalExtent source) {
        super(source);
    }

    @Override
    public double getMinResolved(Optional<Resolution> resolution, boolean do3D) {
        return maybeDivideBy(getMin(), resolution, do3D);
    }

    @Override
    public double getMaxResolved(Optional<Resolution> resolution, boolean do3D) {
        return maybeDivideBy(getMax(), resolution, do3D);
    }

    @Override
    public Bound duplicate() {
        return new BoundPhysicalExtent(this);
    }

    /**
     * Adjusts a value based on the provided resolution.
     *
     * @param value the value to adjust
     * @param resolution an optional resolution to consider
     * @param do3D whether to consider 3D resolution (if available)
     * @return the adjusted value
     */
    private static double maybeDivideBy(
            double value, Optional<Resolution> resolution, boolean do3D) {
        if (resolution.isPresent()) {
            return value / minResolutionComponent(resolution.get(), do3D);
        } else {
            return value;
        }
    }

    /**
     * Calculates the minimum resolution component.
     *
     * @param resolution the resolution to consider
     * @param do3D whether to consider 3D resolution
     * @return the minimum resolution component
     */
    private static double minResolutionComponent(Resolution resolution, boolean do3D) {
        double min2D = Math.min(resolution.x(), resolution.y());
        if (do3D) {
            return Math.min(min2D, resolution.z());
        } else {
            return min2D;
        }
    }
}
