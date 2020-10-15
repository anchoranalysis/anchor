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
package org.anchoranalysis.image.object.scale;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.core.index.GetOperationFailedException;
import org.anchoranalysis.image.extent.box.BoundedList;
import org.anchoranalysis.image.extent.scale.ScaleFactor;

/**
 * A list of elements with an object-mask representation that have been scaled.
 *
 * <p>When scaling object-masks, so as to keep boundaries between objects exact, objects need to be
 * scaled collectively using a particular interpolation procedure rather than collectively.
 *
 * <p>If any elements overlap with other elements, they are scaled separately independently of the
 * others.
 *
 * @author Owen Feehan
 */
public class ScaledElements<T> {

    private final Map<T, T> elementsScaled;

    /**
     * Creates elements to be scaled.
     *
     * @param elements element to be scaled (after possibly a preoperation)
     * @param scaleFactor how much to scale the elements by
     * @param preOperation operation applied to each element before it is scaled (e.g. flattening)
     * @param postOperation operation applied to each element after it is scaled (e.g. flattening)
     * @throws CreateException
     */
    ScaledElements(
            List<T> elements,
            ScaleFactor scaleFactor,
            Optional<UnaryOperator<T>> preOperation,
            Optional<UnaryOperator<T>> postOperation,
            AccessObjectMask<T> access)
            throws CreateException {

        if (elements.isEmpty()) {
            // Early exit if the collection is empty
            elementsScaled = new HashMap<>();
            return;
        }

        /** Creates a list of elements with a bounding-box around all elements */
        BoundedList<T> boundedElements =
                BoundedList.createFromList(elements, access::boundingBoxFor);

        ScaledLabels<T> labels =
                new ScaledLabels<>(boundedElements, preOperation, scaleFactor, access);
        elementsScaled = labels.buildMapOfAllScaledObjects(postOperation);
    }

    /**
     * Gets the scaled version of an element given the unscaled element.
     *
     * <p>The unscaled element must be an identical to that passed into the constructor.
     *
     * @param unscaledElement the unscaled-element
     * @return the corresponding scaled element
     * @throws GetOperationFailedException if the unscaled element was never passed to the
     *     constructor
     */
    public T scaledObjectFor(T unscaledElement) throws GetOperationFailedException {
        T scaled = elementsScaled.get(unscaledElement);
        if (scaled == null) {
            throw new GetOperationFailedException(
                    unscaledElement.toString(),
                    "No scaled-elements exists in the collection corresponding to the input element");
        }
        return scaled;
    }

    /**
     * Returns the scaled-elements as a collection but without preserving the original element
     * order.
     *
     * <p>The total number of objects is identical to the unscaled objects, just the order may
     * differ.
     *
     * @return a collection with the scaled-objects
     */
    public Collection<T> asCollectionOrderNotPreserved() {
        return elementsScaled.values();
    }

    /**
     * Returns a list of scaled-elements corresponding to a list of unscaled-elements
     *
     * @param unscaledElements the list of unscaled-elements (all items must have been previously
     *     passed to the constructor, but the order need not be identical).
     * @return a newly created list where each elements is respectively the scaled-element for the
     *     corresponding input element
     */
    public List<T> asListOrderPreserved(List<T> unscaledElements) {
        return FunctionalList.mapToList(unscaledElements, elementsScaled::get);
    }

    /**
     * Number of scaled-elements.
     *
     * @return the number of elements.
     */
    public int size() {
        return elementsScaled.size();
    }
}
