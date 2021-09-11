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

package org.anchoranalysis.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.bean.exception.BeanStrangeException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperFindChildren {

    /**
     * Finds all bean-fields that are instance of a certain class.
     *
     * <p>All immediate children are checked, and any items in immediate lists.
     *
     * @param bean the bean whose fields will be searched.
     * @param listFields the list of fields associated with the bean.
     * @param match the class that a field must be assignable from (equal to or inherit from).
     * @return a newly created list of the bean-fields that are a instance of {@code match}.
     * @throws BeanMisconfiguredException if a bean is misconfigured.
     */
    public static <T extends AnchorBean<?>> List<T> findChildrenOfClass(
            AnchorBean<?> bean, List<Field> listFields, Class<?> match)
            throws BeanMisconfiguredException {

        List<T> out = new ArrayList<>();

        try {
            for (Field field : listFields) {
                FieldAccessor.fieldFromBean(bean, field)
                        .ifPresent(value -> maybeAdd(value, match, out));
            }
        } catch (IllegalAccessException e) {
            throw new BeanStrangeException(
                    "While using reflection, a permissions problem occurred", e);
        }

        return out;
    }

    /**
     * Adds an object to a list, only if it matches our condition.
     *
     * @param fieldValue the object to consider adding
     * @param match the class that a field must be assignable from (equal to or inherit from)
     * @param out a list of objects that matched
     */
    @SuppressWarnings("unchecked")
    private static <T extends AnchorBean<?>> void maybeAdd(
            Object fieldValue, Class<?> match, List<T> out) {

        if (fieldValue instanceof AnchorBean) {
            AnchorBean<?> valueCast = (AnchorBean<?>) fieldValue;

            if (match.isAssignableFrom(valueCast.getClass())) {
                out.add((T) valueCast);
            }
        }

        // If it's a list, we assume it's a list of IBeans
        if (fieldValue instanceof Collection) {
            Collection<AnchorBean<?>> valueCast = (Collection<AnchorBean<?>>) fieldValue;
            maybeAddCollection(valueCast, match, out);
        }
    }

    /**
     * Adds an object to a list, only if it matches our condition.
     *
     * @param list considers all objects in this list
     * @param match the class to be matched
     * @param out out a list of objects that matched
     */
    @SuppressWarnings("unchecked")
    private static <T extends AnchorBean<?>> void maybeAddCollection(
            Collection<AnchorBean<?>> list, Class<?> match, List<T> out) {

        for (AnchorBean<?> item : list) {
            if (item != null && match.isAssignableFrom(item.getClass())) {
                out.add((T) item);
            }
        }
    }
}
