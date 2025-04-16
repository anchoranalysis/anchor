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

import java.io.Serializable;
import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.GroupingRoot;
import org.anchoranalysis.image.core.dimensions.Resolution;

/**
 * Abstract base class for defining bounds on marks in a marked point process.
 *
 * <p>This class extends {@link AnchorBean} and implements {@link Serializable} to provide
 * a foundation for various types of bounds that can be applied to marks.</p>
 */
@GroupingRoot
public abstract class MarkBounds extends AnchorBean<MarkBounds> implements Serializable {

    private static final long serialVersionUID = 0;

    /**
     * Gets the resolved minimum value of the bound, considering resolution and dimensionality.
     *
     * @param resolution an optional resolution to consider when resolving the bound
     * @param do3D whether to consider 3D resolution (if available)
     * @return the resolved minimum value
     */
    public abstract double getMinResolved(Optional<Resolution> resolution, boolean do3D);

    /**
     * Gets the resolved maximum value of the bound, considering resolution and dimensionality.
     *
     * @param resolution an optional resolution to consider when resolving the bound
     * @param do3D whether to consider 3D resolution (if available)
     * @return the resolved maximum value
     */
    public abstract double getMaxResolved(Optional<Resolution> resolution, boolean do3D);

    /**
     * Calculates the resolved minimum and maximum values as a {@link ResolvedBound}.
     *
     * @param resolution an optional resolution to consider when resolving the bound
     * @param do3D whether to consider 3D resolution (if available)
     * @return a {@link ResolvedBound} containing the resolved minimum and maximum values
     */
    public ResolvedBound calculateMinMax(Optional<Resolution> resolution, boolean do3D) {
        return new ResolvedBound(
                getMinResolved(resolution, do3D), getMaxResolved(resolution, do3D));
    }
}