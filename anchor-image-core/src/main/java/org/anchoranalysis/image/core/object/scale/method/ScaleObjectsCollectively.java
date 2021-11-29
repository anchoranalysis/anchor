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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import org.anchoranalysis.image.core.object.scale.AccessObjectMask;
import org.anchoranalysis.image.core.object.scale.ScaledElements;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.scale.ScaleFactor;

/**
 * An instance of {@link ScaledElements} that scales objects collectively so as to tightly preserve
 * the boundary between objects.
 *
 * <p>This does not reliable tolerate any overlap between objects (i.e. that any two objects
 * intersect). It expects non-overlapping input objects, and produces non-overlapping output
 * objects.
 *
 * @author Owen Feehan
 */
class ScaleObjectsCollectively implements ObjectScalingMethod {

    @Override
    public <T> Map<T, T> scaleElements(
            List<T> elements,
            ScaleFactor scaleFactor,
            Optional<UnaryOperator<T>> preOperation,
            Optional<UnaryOperator<T>> postOperation,
            AccessObjectMask<T> access) {
        HashMap<T, T> map = new HashMap<>();
        for (int i = 0; i < elements.size(); i++) {
            T elementUnscaled = elements.get(i);
            T elementScaled =
                    scaleElementIndividually(
                            elementUnscaled, scaleFactor, i, preOperation, postOperation, access);
            map.put(elementUnscaled, elementScaled);
        }
        return map;
    }

    private <T> T scaleElementIndividually(
            T unscaled,
            ScaleFactor scaleFactor,
            int index,
            Optional<UnaryOperator<T>> preOperation,
            Optional<UnaryOperator<T>> postOperation,
            AccessObjectMask<T> access) {
        T element = unscaled;
        if (preOperation.isPresent()) {
            element = preOperation.get().apply(element);
        }
        ObjectMask objectScaled = access.objectFor(element).scale(scaleFactor);
        element = access.createFrom(index, objectScaled);
        if (postOperation.isPresent()) {
            element = postOperation.get().apply(element);
        }
        return element;
    }
}
