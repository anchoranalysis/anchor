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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Determines which instance of {@link ObjectScalingMethod} to apply.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ObjectScalingMethodFactory {

    /**
     * Scale objects collectively, so as to preserve a tight border between objects. But objects
     * must be guaranteed not to overlap.
     */
    private static final ObjectScalingMethod COLLECTIVELY = new ScaleObjectsCollectively();

    /**
     * Scale objects independently, without preserving a tight border between objects. But objects
     * are allowed to overlap.
     */
    private static final ObjectScalingMethod INDEPENDENTLY = new ScaleObjectsIndependently();

    /**
     * Determines which method to use, given circumstances.
     *
     * @param overlappingObjects whether objects-to-scaled may overlap with each other.
     * @return an appropriate scaling method for the circumstance.
     */
    public static ObjectScalingMethod of(boolean overlappingObjects) {
        if (overlappingObjects) {
            return INDEPENDENTLY;
        } else {
            return COLLECTIVELY;
        }
    }
}
