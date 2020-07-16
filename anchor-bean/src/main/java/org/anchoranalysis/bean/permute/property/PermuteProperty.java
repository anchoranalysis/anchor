/*-
 * #%L
 * anchor-bean
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
package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.permute.setter.PermutationSetter;
import org.anchoranalysis.bean.permute.setter.PermutationSetterException;
import org.anchoranalysis.core.error.OperationFailedException;

/** @param <T> Type of permutations */
public abstract class PermuteProperty<T> extends AnchorBean<PermuteProperty<T>> {

    /**
     * Converts a particular value to a string representation
     *
     * @param value
     */
    public abstract String nameForPropValue(T value) throws OperationFailedException;

    public abstract PermutationSetter createSetter(AnchorBean<?> parentBean)
            throws PermutationSetterException;

    /**
     * An iterator (of size numPermutations()) with the values for the property
     *
     * @return a new iterator for the permutations that are possible
     */
    public abstract Iterator<T> propertyValues();
}
