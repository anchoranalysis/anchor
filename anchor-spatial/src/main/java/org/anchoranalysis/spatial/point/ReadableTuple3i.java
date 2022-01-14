package org.anchoranalysis.spatial.point;

/*-
 * #%L
 * anchor-core
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan
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
import java.util.function.IntPredicate;
import org.anchoranalysis.core.functional.unchecked.IntBinaryPredicate;
import org.anchoranalysis.spatial.axis.Axis;
import org.anchoranalysis.spatial.axis.AxisConverter;

/**
 * A tuple with three-values, respectively for the X, Y and Z dimensions.
 *
 * <p>This is convenient to provide non-mutable (read-only) access to a tuple's values.
 *
 * <p>A natural ordering is imposed by comparing the Z, Y, X values in that order.
 *
 * @author Owen Feehan
 */
public interface ReadableTuple3i extends Serializable, Comparable<ReadableTuple3i> {

    /**
     * X-axis component of the tuple.
     *
     * @return the component value.
     */
    int x();

    /**
     * Y-axis component of the tuple.
     *
     * @return the component value.
     */
    int y();

    /**
     * Z-axis component of the tuple.
     *
     * @return the component value.
     */
    int z();

    /**
     * A component of a tuple corresponding to a particular axis.
     *
     * @param axis the axis.
     * @return the component of the tuple corresponding to that axis.
     */
    int valueByDimension(Axis axis);

    /**
     * A component of a tuple corresponding to a particular dimension by index.
     *
     * @param dimensionIndex the index corresponding to an axis, as per {@link AxisConverter}.
     * @return the component of the tuple corresponding to that axis.
     */
    int valueByDimension(int dimensionIndex);

    /**
     * Creates a copy of the current tuple, but with a different Z-component value.
     *
     * @param zValueToAssign the value to assign in the Z-axis.
     * @return a copy of the current object with changed Z-component value.
     */
    ReadableTuple3i duplicateChangeZ(int zValueToAssign);

    /**
     * Whether the values in all dimensions satisfy a predicate.
     *
     * @param predicate the test applied to each dimension's value.
     * @return true if the values in <b>all</b> three dimensions satisfy the predicate, false
     *     otherwise.
     */
    default boolean matchAllDimensions(IntPredicate predicate) {
        return predicate.test(x()) && predicate.test(y()) && predicate.test(z());
    }

    /**
     * Whether the values in each dimension satisfy a predicate, where the value in the current
     * object is the left argument, and the corresponding value in {@code point} forms the right
     * argument.
     *
     * @param point the point that forms the second right-most value in the predicate.
     * @param predicate the test applied to each dimension's value (and corresponding value in
     *     {@code point}).
     * @return true if <b>all</b> three dimensions satisfy the predicate, false otherwise.
     */
    default boolean matchAllDimensions(ReadableTuple3i point, IntBinaryPredicate predicate) {
        return predicate.test(x(), point.x())
                && predicate.test(y(), point.y())
                && predicate.test(z(), point.z());
    }

    @Override
    default int compareTo(ReadableTuple3i other) {
        int compareZ = Integer.compare(z(), other.z());
        if (compareZ != 0) {
            return compareZ;
        }

        int compareY = Integer.compare(y(), other.y());
        if (compareY != 0) {
            return compareY;
        }

        int compareX = Integer.compare(x(), other.x());
        if (compareX != 0) {
            return compareX;
        }

        return 0;
    }
}
