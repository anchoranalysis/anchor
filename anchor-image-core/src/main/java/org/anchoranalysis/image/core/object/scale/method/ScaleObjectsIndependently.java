/*-
 * #%L
 * anchor-image-core
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
package org.anchoranalysis.image.core.object.scale.method;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.object.scale.AccessObjectMask;
import org.anchoranalysis.image.core.object.scale.ScaledElements;
import org.anchoranalysis.spatial.box.BoundedList;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * An instance of {@link ScaledElements} that scales each object in the collection indepednently of
 * the others.
 *
 * <p>Unlike {@code ScaleObjectsCollectively}, this can be reliably used with overlapping objects
 * (i.e. when any two objects in the input collection intersect with each other).
 *
 * @author Owen Feehan
 */
class ScaleObjectsIndependently implements ObjectScalingMethod {

    @Override
    public <T> Map<T, T> scaleElements(
            List<T> elements,
            ScaleFactor scaleFactor,
            Optional<UnaryOperator<T>> preOperation,
            Optional<UnaryOperator<T>> postOperation,
            AccessObjectMask<T> access)
            throws OperationFailedException {
        /** Creates a list of elements with a bounding-box around all elements */
        BoundedList<T> boundedElements =
                BoundedList.createFromList(elements, access::boundingBoxFor);

        try {
            ScaledLabels<T> labels =
                    new ScaledLabels<>(boundedElements, preOperation, scaleFactor, access);
            return labels.buildMapOfAllScaledObjects(postOperation);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
