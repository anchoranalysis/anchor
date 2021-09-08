package org.anchoranalysis.core.index.range;

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

/**
 * A range of discrete integers, where one can always find the succeeding or preceding index.
 *
 * <p>It can be navigated to find where indexes are with {@link #nextIndex}, {@link #previousIndex}
 * etc.
 *
 * @author Owen Feehan
 */
public interface IndexRangeSeek extends IndexRange {

    /**
     * Finds the <i>next</i> index in the range after {@code index}.
     *
     * @param index the base index, for which a next index is to be found.
     * @return the next index after {@code index} or -1 if none exists.
     */
    int nextIndex(int index);

    /**
     * Finds the <i>previous</i> index in the range before {@code index}.
     *
     * @param index the base index, for which a previous index is to be found.
     * @return the previous index before {@code index} or -1 if none exists.
     */
    int previousIndex(int index);

    /**
     * Finds the <i>previous</i> index in the range before {@code index}, or returns {@code index}
     * itself if it's a valid index.
     *
     * @param index the base index, for which a previous (or equal) index is to be found.
     * @return {code index} itself it is a valid index, or the previous index before {@code index}
     *     or -1 if none exists.
     */
    int previousEqualIndex(int index);
}
