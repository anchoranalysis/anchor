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
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;

/**
 * Utilities to access a {@link Field} of an {@link AnchorBean}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FieldAccessor {

    /**
     * Retrieves the value of a {@link Field} from a {@link AnchorBean}.
     *
     * @param bean the bean
     * @param field the field to retrieve a value from
     * @return the value, it it exists, or {@code Optional.empty()} if it doesn't exist <b>and</b>
     *     the bean is marked as optional.
     * @throws IllegalAccessException if reflection tries to access a property it has no permissions
     *     for.
     * @throws BeanMisconfiguredException if no value exists for the field (and it's not marked as
     *     optional).
     */
    public static Optional<Object> fieldFromBean(AnchorBean<?> bean, Field field)
            throws IllegalAccessException, BeanMisconfiguredException {
        // If it's non-optional, then we insist it's non-null
        Object value = field.get(bean);
        if (value == null) {
            if (isFieldAnnotatedAsOptional(field)) {
                return Optional.empty();
            } else {
                throwMissingPropertyException(field.getName(), bean.getBeanName());
            }
        }
        return Optional.of(value);
    }

    /**
     * Is a particular {@link Field} on a {@link AnchorBean} marked as optional?
     *
     * @param field the field
     * @return true iff it is marked as optional.
     */
    public static boolean isFieldAnnotatedAsOptional(Field field) {
        return field.isAnnotationPresent(OptionalBean.class);
    }

    private static void throwMissingPropertyException(String fieldName, String className)
            throws BeanMisconfiguredException {
        throw new BeanMisconfiguredException(
                String.format("Property %s of class %s must be non-null", fieldName, className));
    }
}
