package org.anchoranalysis.math.arithmetic;

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

import java.util.ArrayList;

/**
 * A collection of {@link RunningSum} where an operation is executed on all objects in the
 * collection.
 *
 * @author Owen Feehan
 */
public class RunningSumCollection {

    private ArrayList<RunningSum> list;

    /**
     * Constructor. Creates a collection with a certain number of (zeroed) {@link RunningSum}.
     *
     * @param size number of items in the collection
     */
    public RunningSumCollection(int size) {
        list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(new RunningSum());
        }
    }

    /**
     * An individual item in the collection.
     *
     * @param index index of the item
     * @return the individual item
     */
    public RunningSum get(int index) {
        return list.get(index);
    }

    /** Resets all items to zero. */
    public void reset() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).reset();
        }
    }

    /**
     * Calculate the mean of each item and reset to zero.
     *
     * @return an array with a mean corresponding to each item in the collection
     */
    public double[] meanAndReset() {

        double[] res = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            res[i] = list.get(i).meanAndReset();
        }
        return res;
    }
}
