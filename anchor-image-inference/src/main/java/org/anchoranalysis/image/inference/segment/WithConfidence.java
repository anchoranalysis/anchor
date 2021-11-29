/*-
 * #%L
 * anchor-plugin-opencv
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

package org.anchoranalysis.image.inference.segment;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Wraps an element with a confidence-score.
 *
 * <p>A natural ordering exists, highest confidences ahead of lower confidences.
 *
 * @param <T> element to be associated with a confidence score
 */
@AllArgsConstructor
@EqualsAndHashCode
public class WithConfidence<T> implements Comparable<WithConfidence<T>> {

    /** The underlying element with whom a confidence is associated. */
    @Getter private T element;

    /** The confidence associated with {@code element}. */
    @Getter private double confidence;

    /**
     * Maps the existing object to another object, while retaining an identical confidence score.
     *
     * @param <S> type to map to
     * @param transform converts the existing element into the new type
     * @return a newly created {@link WithConfidence} object containing the transformed element but
     *     preserving the confidence.
     */
    public <S> WithConfidence<S> map(Function<T, S> transform) {
        return new WithConfidence<>(transform.apply(element), confidence);
    }

    @Override
    public int compareTo(WithConfidence<T> other) {
        return Double.compare(other.confidence, confidence);
    }

    @Override
    public String toString() {
        return String.format("confidence=%.2f, %s", confidence, element);
    }
}
