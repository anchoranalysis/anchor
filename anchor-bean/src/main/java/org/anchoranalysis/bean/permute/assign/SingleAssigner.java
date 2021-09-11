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

package org.anchoranalysis.bean.permute.assign;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;

/**
 * Sets a permutation from a list of fields
 *
 * <p>All fields must refer to either an {@link AnchorBean} or (as a corner case) a {@link
 * java.util.List} of containing at least one item of type {@link AnchorBean}. The first item of the
 * list will be taken as the corresponding Anchor-Bean.
 *
 * @author Owen Feehan
 */
class SingleAssigner implements PermutationAssigner {

    /** Intermediate properties that must be traversed before getting to the final field */
    private List<Field> listFields = new ArrayList<>();

    public SingleAssigner() {
        // Nothing is initialized, one must do it themselves
    }

    public SingleAssigner(Field field) {
        addField(field);
    }

    public void addField(Field field) {
        this.listFields.add(field);
    }

    @Override
    public void assignValue(AnchorBean<?> bean, Object value) throws AssignPermutationException {

        try {
            int numberIntermediate = listFields.size() - 1;

            // We iterate through all the intermediate fields
            AnchorBean<?> currentBean = bean;
            for (int i = 0; i < numberIntermediate; i++) {
                Field field = listFields.get(i);
                currentBean = FindBean.beanFor(field, currentBean);
            }

            // Now do the final field, where we actually set the value
            Field finalField = listFields.get(numberIntermediate);

            Object valMaybeConverted = maybeConvert(value, finalField.getType());
            finalField.set(currentBean, valMaybeConverted); // NOSONAR

        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new AssignPermutationException(
                    "An illegal argument or access exception occurred setting a permutation on the bean");
        }
    }

    /**
     * Harding coding a special case where the object conversion needs to occur to a {@link String}.
     *
     * @return an object converted to a {@link String} when {@code targetType} is {@link String}.
     */
    private static Object maybeConvert(Object val, Class<?> targetType) {

        if (targetType.equals(String.class)) {
            return val.toString();
        }
        return val;
    }
}
