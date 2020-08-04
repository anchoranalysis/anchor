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
import java.util.List;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.error.BeanStrangeException;
import org.anchoranalysis.core.error.OperationFailedException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
class HelperBeanFields {

    public static String describeChildBeans(AnchorBean<?> bean) {

        try {

            List<Field> listFields = bean.getOrCreateBeanFields();

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < listFields.size(); i++) {
                Field field = listFields.get(i);

                Object value = field.get(bean);

                // If it's non-optional, then we insist it's non-null
                if (value == null) {
                    if (field.isAnnotationPresent(OptionalBean.class)) {
                        continue;
                    } else {
                        throwMissingPropertyException(field.getName(), bean.getBeanName());
                    }
                }

                if (i != 0) {
                    sb.append(", ");
                }

                sb.append(field.getName());
                sb.append("=");
                sb.append(describeField(field, bean));
            }
            return sb.toString();

        } catch (IllegalAccessException | OperationFailedException | BeanMisconfiguredException e) {
            throw new BeanStrangeException("Failed to describe child beans", e);
        }
    }

    // We use Field instead of PropertyDescriptor has they seem to be much faster to access with
    // reflection. Otherwise it's very slow
    // And we make sure we set the setAccessible(true) as this reportedly disables access checks,
    // and makes access much faster.
    public static List<Field> createListBeanPropertyFields(Class<?> c) {

        List<Field> fieldsOut = new ArrayList<>();

        List<Field> listFields = HelperReflection.findAllFields(c);

        for (Field field : listFields) {
            if (field.isAnnotationPresent(BeanField.class)) {
                field.setAccessible(true);
                fieldsOut.add(field);
            }
        }

        return fieldsOut;
    }

    private static String describeBean(AnchorBean<?> bean) {

        StringBuilder sb = new StringBuilder();
        sb.append(bean.getBeanName());
        sb.append("(");
        sb.append(describeChildBeans(bean));
        sb.append(")");
        return sb.toString();
    }

    static void throwMissingPropertyException(String fieldName, String className)
            throws BeanMisconfiguredException {
        throw new BeanMisconfiguredException(
                String.format("Property %s of class %s must be non-null", fieldName, className));
    }

    private static String describeField(Field field, AnchorBean<?> bean)
            throws OperationFailedException {
        try {

            Object val = field.get(bean);
            if (val instanceof AnchorBean) {

                AnchorBean<?> valCast = (AnchorBean<?>) val;
                return describeBean(valCast);
            }

            if (val instanceof List) {
                return "list";
            }

            if (val instanceof String) {
                return "'" + (String) val + "'";
            }

            if (val instanceof Integer) {
                return Integer.toString((Integer) val);
            }

            if (val instanceof Double) {
                return Double.toString((Double) val);
            }

            if (val instanceof Float) {
                return Float.toString((Float) val);
            }

            if (val instanceof Byte) {
                return Byte.toString((Byte) val);
            }

            if (val instanceof Short) {
                return Short.toString((Short) val);
            }

            if (val instanceof Long) {
                return Long.toString((Long) val);
            }

            if (val instanceof Boolean) {
                return (boolean) val ? "true" : "false";
            }

            throw new OperationFailedException("Unknown bean type");

        } catch (IllegalAccessException e) {
            throw new OperationFailedException(e);
        }
    }
}
