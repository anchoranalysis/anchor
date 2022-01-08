/*-
 * #%L
 * anchor-plugin-image
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

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.core.object.scale.AccessObjectMask;
import org.anchoranalysis.image.core.object.scale.Scaler;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.ReadableTuple3i;

/**
 * Provides access for the {@link Scaler} to the object-representation of {@code
 * WithConfidence<ObjectMask>}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PUBLIC)
class AccessSegmentedObjects implements AccessObjectMask<WithConfidence<ObjectMask>> {

    private final List<WithConfidence<ObjectMask>> listUnscaled;

    @Override
    public ObjectMask objectFor(WithConfidence<ObjectMask> element) {
        return element.getElement();
    }

    @Override
    public WithConfidence<ObjectMask> shiftBy(
            WithConfidence<ObjectMask> element, ReadableTuple3i quantity) {
        return element.map(existingObject -> existingObject.shiftBy(quantity));
    }

    @Override
    public WithConfidence<ObjectMask> clipTo(WithConfidence<ObjectMask> element, Extent extent) {
        return element.map(existingObject -> existingObject.clampTo(extent));
    }

    @Override
    public WithConfidence<ObjectMask> createFrom(int index, ObjectMask object) {
        return listUnscaled.get(index).map(existingObject -> object);
    }
}
