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

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import org.anchoranalysis.bean.exception.BeanStrangeException;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.functional.FunctionalList;

/**
 * Routines that operation on the fields of an {@link AnchorBean}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperBeanFields {

    /**
     * Creates a human-readable description of all child-beans contained in {@code bean}.
     *
     * @param bean the bean whose children are described.
     * @return a human-readable description.
     */
    public static String describeChildBeans(AnchorBean<?> bean) {
        try {
            List<String> descriptions =
            		FunctionalList.mapToListOptional(
                            bean.fields(),
                            OperationFailedException.class,
                            field -> maybeDescribeChildBean(bean, field));
            return String.join(", ", descriptions);

        } catch (OperationFailedException e) {
            throw new BeanStrangeException("Failed to describe child beans", e);
        }
    }

    /**
     * Creates a list of all <i>bean property</i> fields associated with the bean.
     *
     * <p>{@link Field} is used in place of {@link PropertyDescriptor} as it seems to be much faster
     * to access with reflection. Otherwise it's quite slow. And we make sure we set {@code
     * setAccessible(true)} as this reportedly disables access checks, and makes access much faster.
     *
     * @param clss the class of the bean, whose fields should be listed
     * @return a newly created list of all the bean-property fields associated with {@code clss}
     */
    public static List<Field> createListBeanPropertyFields(Class<?> clss) {

        List<Field> allFields = HelperReflection.findAllFields(clss);

        return FunctionalList.filterAndMapToList(
                allFields,
                field -> field.isAnnotationPresent(BeanField.class),
                field -> {
                    field.setAccessible(true); // NOSONAR
                    return field;
                });
    }

    private static String describeBean(AnchorBean<?> bean) {
        StringBuilder builder = new StringBuilder();
        builder.append(bean.getBeanName());
        builder.append("(");
        builder.append(describeChildBeans(bean));
        builder.append(")");
        return builder.toString();
    }

    private static Optional<String> maybeDescribeChildBean(AnchorBean<?> bean, Field field)
            throws OperationFailedException {

        try {
            FieldAccessor.fieldFromBean(bean, field);

            return Optional.of(String.format("%s=%s", field.getName(), describeField(field, bean)));
        } catch (IllegalArgumentException
                | OperationFailedException
                | BeanMisconfiguredException
                | IllegalAccessException e) {
            throw new OperationFailedException(e);
        }
    }

    private static String describeField(Field field, AnchorBean<?> bean)
            throws OperationFailedException {
        try {

            Object value = field.get(bean);
            if (value instanceof AnchorBean) {
                return describeBean((AnchorBean<?>) value);
            }

            if (value instanceof List) {
                return "list";
            }

            if (value instanceof String) {
                return "'" + (String) value + "'";
            }

            if (value instanceof Integer) {
                return Integer.toString((Integer) value);
            }

            if (value instanceof Double) {
                return Double.toString((Double) value);
            }

            if (value instanceof Float) {
                return Float.toString((Float) value);
            }

            if (value instanceof Byte) {
                return Byte.toString((Byte) value);
            }

            if (value instanceof Short) {
                return Short.toString((Short) value);
            }

            if (value instanceof Long) {
                return Long.toString((Long) value);
            }

            if (value instanceof Boolean) {
                return (boolean) value ? "true" : "false";
            }

            throw new OperationFailedException("Unknown bean type");

        } catch (IllegalAccessException e) {
            throw new OperationFailedException(e);
        }
    }
}
