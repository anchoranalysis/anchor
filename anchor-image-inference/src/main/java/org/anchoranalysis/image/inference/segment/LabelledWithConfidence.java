/*-
 * #%L
 * anchor-plugin-image
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
 * Like {@link WithConfidence} but additionally adds a label.
 *
 * @param <T> element to be associated with a confidence score.
 * @author Owen Feehan
 */
@AllArgsConstructor
@EqualsAndHashCode
public class LabelledWithConfidence<T> implements Comparable<LabelledWithConfidence<T>> {

    /** The label associated with the element. */
    @Getter private final String label;

    /** The element with associated confidence. */
    @Getter private final WithConfidence<T> withConfidence;

    /**
     * Create for given element, confidence and label.
     *
     * @param element the underlying element with whom a confidence and label is associated.
     * @param confidence the confidence associated with {@code element}.
     * @param label the label associated with the element.
     */
    public LabelledWithConfidence(T element, double confidence, String label) {
        this.withConfidence = new WithConfidence<>(element, confidence);
        this.label = label;
    }

    /**
     * Maps the existing object to another object, while retaining an identical confidence score.
     *
     * @param <S> type to map to
     * @param transform converts the existing element into the new type
     * @return a newly created {@link WithConfidence} object containing the transformed element but
     *     preserving the confidence.
     */
    public <S> LabelledWithConfidence<S> map(Function<T, S> transform) {
        S transformed = transform.apply(withConfidence.getElement());
        return new LabelledWithConfidence<>(transformed, withConfidence.getConfidence(), label);
    }

    /**
     * The underlying element with whom a confidence and label is associated.
     *
     * @return the element.
     */
    public T getElement() {
        return withConfidence.getElement();
    }

    /**
     * The confidence associated with {@code element}.
     *
     * @return the confidence.
     */
    public double getConfidence() {
        return withConfidence.getConfidence();
    }

    @Override
    public int compareTo(LabelledWithConfidence<T> other) {
        int confidenceComparison = withConfidence.compareTo(other.withConfidence);
        if (confidenceComparison == 0) {
            return label.compareTo(other.label);
        } else {
            return confidenceComparison;
        }
    }

    @Override
    public String toString() {
        return String.format("label=%s,%s", label, withConfidence);
    }
}
