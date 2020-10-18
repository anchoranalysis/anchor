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
package org.anchoranalysis.image.core.object.label;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;

/**
 * A helper class to create a map from input-elements to output-elements (labelled-elements with an
 * operation applied)
 *
 * @author Owen Feehan
 * @param <T> element-type
 */
@AllArgsConstructor
class MapForLabelsCreator<T> {

    /** a map from a label to the corresponding input-element */
    private Map<Integer, T> input;

    /** the labelled-elements for each label in the sequence */
    private List<T> labelled;

    /** an operation to apply after labelling, but before the object is placed in the map */
    private UnaryOperator<T> operationAfterLabelling;

    /** Minimum label-value inclusive */
    private int minLabelInclusive;

    /**
     * Creates a map from the input-elements to output-elements (their derived labelled elements
     * with an operation applied)
     *
     * @return the newly created map
     */
    public Map<T, T> createMapForLabels() {

        Stream<Integer> indexStream =
                IntStream.range(0, labelled.size()).mapToObj(Integer::valueOf);

        return indexStream.collect(
                Collectors.toMap(this::inputElementForLabel, this::outputElementForIndex));
    }

    private T inputElementForLabel(int index) {
        return input.get(minLabelInclusive + index);
    }

    private T outputElementForIndex(int index) {
        return operationAfterLabelling.apply(labelled.get(index));
    }
}
