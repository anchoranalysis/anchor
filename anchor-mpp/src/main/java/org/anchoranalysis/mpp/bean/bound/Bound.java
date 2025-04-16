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
import org.anchoranalysis.image.core.dimensions.Resolution;

/**
 * An abstract class representing an upper and lower bound.
 *
 * <p>This class extends {@link MarkBounds} to provide a framework for implementing
 * specific types of bounds with minimum and maximum values.</p>
 */
public abstract class Bound extends MarkBounds {

    private static final long serialVersionUID = -5447041367811327604L;

    /**
     * Creates a duplicate of this bound.
     *
     * @return a new instance of {@link Bound} with the same properties as this one
     */
    public abstract Bound duplicate();

    /**
     * Resolves the bound based on the given resolution and dimensionality.
     *
     * @param resolution an optional resolution to consider when resolving the bound
     * @param do3D whether to consider 3D resolution (if available)
     * @return a {@link ResolvedBound} instance representing the resolved minimum and maximum values
     */
    public ResolvedBound resolve(Optional<Resolution> resolution, boolean do3D) {
        return new ResolvedBound(
                getMinResolved(resolution, do3D), getMaxResolved(resolution, do3D));
    }

    /**
     * Scales the bound by a multiplication factor.
     *
     * @param multFactor the factor to multiply the bound values by
     */
    public abstract void scale(double multFactor);
}