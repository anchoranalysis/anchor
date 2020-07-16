/*-
 * #%L
 * anchor-image-feature
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
/* (C)2020 */
package org.anchoranalysis.image.feature.object.input;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.feature.input.FeatureInputNRG;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.image.object.ObjectMask;

/**
 * An input representing a single object-mask (with maybe an NRG-stack associated)
 *
 * <p>Equals and hash-code must be sensibly defined as these inputs can be used as keys in a cache.
 * The equals implementation assumes equals of {@link ObjectMask} is shallow and computationally
 * inexpensive..
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public class FeatureInputSingleObject extends FeatureInputNRG {

    @Getter private ObjectMask object;

    public FeatureInputSingleObject(ObjectMask object) {
        this(object, Optional.empty());
    }

    public FeatureInputSingleObject(ObjectMask object, NRGStackWithParams nrgStack) {
        this(object, Optional.of(nrgStack));
    }

    public FeatureInputSingleObject(ObjectMask object, Optional<NRGStackWithParams> nrgStack) {
        super(nrgStack);
        this.object = object;
    }

    @Override
    public String toString() {
        return object.toString();
    }
}
