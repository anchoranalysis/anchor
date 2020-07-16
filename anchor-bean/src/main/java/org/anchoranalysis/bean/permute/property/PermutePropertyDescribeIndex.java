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
import org.anchoranalysis.bean.StringList;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.permute.setter.PermutationSetter;
import org.anchoranalysis.bean.permute.setter.PermutationSetterException;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Proxies an existing PermuteProperty but replaces the existing naming behaviour
 *
 * <p>Instead, names are determined by an integer lookup table.
 *
 * <p>TODO replace permuteProperty with an automatically generated sequence as it can be inferred
 * from the StringList
 *
 * @param <T> property type
 */
public class PermutePropertyDescribeIndex extends PermuteProperty<Integer> {

    // START BEAN FIELDS
    @BeanField private PermuteProperty<Integer> permuteProperty;

    @BeanField private StringList lookup;
    // END BEAN FIELDS

    @Override
    public Iterator<Integer> propertyValues() {
        return permuteProperty.propertyValues();
    }

    @Override
    public PermutationSetter createSetter(AnchorBean<?> parentBean)
            throws PermutationSetterException {
        return permuteProperty.createSetter(parentBean);
    }

    @Override
    public String nameForPropValue(Integer value) throws OperationFailedException {

        try {
            return lookup.list().get(value);

        } catch (IndexOutOfBoundsException e) {
            throw new OperationFailedException(
                    String.format("No lookup value found for index %d", value));
        }
    }

    public PermuteProperty<Integer> getPermuteProperty() {
        return permuteProperty;
    }

    public void setPermuteProperty(PermuteProperty<Integer> permuteProperty) {
        this.permuteProperty = permuteProperty;
    }

    public StringList getLookup() {
        return lookup;
    }

    public void setLookup(StringList lookup) {
        this.lookup = lookup;
    }
}
