package org.anchoranalysis.spatial.point;

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

import java.util.Comparator;

/**
 * Imposes an ordering on any sub-type of {@link Tuple3i}.
 *
 * <p>The ordering compares the X, Y and Z component values in that order.
 *
 * @author Owen Feehan
 * @param <T> type to compare.
 */
public final class Comparator3i<T extends Tuple3i> implements Comparator<T> {

    @Override
    public int compare(T first, T second) {

        // First order by X
        int cmpX = Integer.compare(first.x(), second.x());
        if (cmpX != 0) {
            return cmpX;
        }

        // Then ordered by Y
        int cmpY = Integer.compare(first.y(), second.y());
        if (cmpY != 0) {
            return cmpY;
        }

        // Then ordered by Z
        int cmpZ = Integer.compare(first.z(), second.z());
        if (cmpZ != 0) {
            return cmpZ;
        }

        return 0;
    }
}
