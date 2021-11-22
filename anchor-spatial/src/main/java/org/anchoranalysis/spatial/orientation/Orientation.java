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

package org.anchoranalysis.spatial.orientation;

import java.io.Serializable;
import java.util.function.BiConsumer;

/**
 * The <a href="https://en.wikipedia.org/wiki/Orientation_(geometry)">orientation</a> (direction) in
 * 2D or 3D Euclidean space.
 *
 * <p>It presumes the existing entity has a neutral orientation along the x-axis.
 *
 * @author Owen Feehan
 */
public abstract class Orientation implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    /**
     * Deep-copies the current instance.
     *
     * @return a deep copy.
     */
    public abstract Orientation duplicate();

    /**
     * Derives a {@link RotationMatrix} that can be applied to rotate an entity
     * <b>anti-clockwise</b> to the current orientation.
     *
     * <p>It presumes the existing entity has a neutral orientation along the x-axis.
     *
     * @return the rotation-matrix.
     */
    public abstract RotationMatrix deriveRotationMatrix();

    /**
     * The dimensionality of space the orientation is valid for.
     *
     * @return 2 or 3.
     */
    public abstract int numberDimensions();

    @Override
    public abstract boolean equals(Object other);

    @Override
    public abstract int hashCode();

    /**
     * Derives a new {@link Orientation} in the opposite (negative) direction to the current
     * instance.
     *
     * <p>A unit step from the origin in the current orientation direction, summed with a unit step
     * in the negative direction, should end in the origin.
     *
     * @return the new {@link Orientation} in the negative direction.
     */
    public abstract Orientation negative();

    /**
     * Consumers descriptive human-readable name-value pairs to describe the current orientation.
     *
     * @param consumer called for each descriptive name-value pair that describes an aspect of the
     *     current orientation.
     */
    public abstract void describeOrientation(BiConsumer<String, String> consumer);
}
