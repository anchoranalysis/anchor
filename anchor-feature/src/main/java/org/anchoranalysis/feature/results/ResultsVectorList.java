/*-
 * #%L
 * anchor-feature
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

package org.anchoranalysis.feature.results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import lombok.NoArgsConstructor;

/**
 * A list of elements of type {@link ResultsVector}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class ResultsVectorList implements Iterable<ResultsVector> {

    private List<ResultsVector> list = new ArrayList<>();

    /**
     * Creates with the list containing a single item.
     *
     * @param results the single item for the list.
     */
    public ResultsVectorList(ResultsVector results) {
        list.add(results);
    }

    /**
     * Add a {@link ResultsVector} to the list in the final position.
     *
     * @param results the results to add.
     */
    public void add(ResultsVector results) {
        list.add(results);
    }

    /**
     * The number of {@link ResultsVector}s in the list.
     *
     * @return the total number.
     */
    public int size() {
        return list.size();
    }

    /**
     * Gets a {@link ResultsVector} at a particular position i nthe list.
     *
     * @param index the position (zero-indexed).
     * @return the vector at the position.
     */
    public ResultsVector get(int index) {
        return list.get(index);
    }

    @Override
    public Iterator<ResultsVector> iterator() {
        return list.iterator();
    }

    /**
     * A stream of {@link ResultsVector}s.
     *
     * @return the stream.
     */
    public Stream<ResultsVector> stream() {
        return list.stream();
    }

    /**
     * Returns {@code true} if the list contains no elements.
     *
     * @return {@code true} if the list contains no elements
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
