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
import java.util.Collection;
import java.util.List;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.bean.annotation.NonEmpty;
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.annotation.Positive;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;

class HelperCheckMisconfigured {

    /**
     * A map between class-types and bean-instances used to populate default-values for beans with
     * certain annotations
     */
    private final BeanInstanceMap defaultInstances;

    HelperCheckMisconfigured(final BeanInstanceMap defaultInstances) {
        this.defaultInstances = defaultInstances;
    }

    /**
     * Checks that a bean's properties are correct
     *
     * <p>(e.g. that required fields are present, values match BeanField annotations etc.)
     *
     * @param obj bean-object
     * @param listFields fields associated with the bean
     * @throws BeanMisconfiguredException
     */
    public void checkMisconfiguredWithFields(AnchorBean<?> obj, List<Field> listFields)
            throws BeanMisconfiguredException {

        String beanName = obj.getClass().getName();
        try {
            for (Field field : listFields) {

                Object value = field.get(obj);

                if (value == null && field.isAnnotationPresent(DefaultInstance.class)) {
                    // If there's no value set, and there's a DefaultInstance annotation, we search
                    // for
                    //  a defaultInstance and throw an error if it doesn't exist
                    value = findDefaultInstance(field.getType());
                    field.set(obj, value);
                }

                // If it's non-optional, then we insist it's non-null
                if (value == null && !field.isAnnotationPresent(OptionalBean.class)) {
                    throw new BeanMisconfiguredException(
                            String.format(
                                    "Property '%s' of object '%s' must be non-null",
                                    field.getName(), beanName));
                }

                // Otherwise, we check the property if we can
                if (value != null) {
                    checkProperty(field, value, beanName);
                }
            }

        } catch (IllegalAccessException e) {
            throw new BeanMisconfiguredException("An exception occurred accessing a property", e);
        }
    }

    /**
     * Searches for an instance to use as a default for a class
     *
     * <p>If no default can be found in the map, an exception is thrown.
     *
     * @param classType the type of the class to find a default for
     * @return the default-instance associated with the class. Null is not possible.
     * @throws BeanMisconfiguredException if no matching default can be found
     */
    private Object findDefaultInstance(Class<?> classType) throws BeanMisconfiguredException {

        Object object = defaultInstances.get(classType);

        if (object == null) {
            throw new BeanMisconfiguredException(
                    String.format(
                            "No default property can be found for class '%s'",
                            classType.getName()));
        }

        return object;
    }

    @SuppressWarnings("unchecked")
    private void checkProperty(Field field, Object fieldValue, String beanName)
            throws BeanMisconfiguredException {

        // If it's another bean we recursively check
        if (fieldValue instanceof AnchorBean) {
            AnchorBean<?> valueCast = (AnchorBean<?>) fieldValue;
            valueCast.checkMisconfigured(defaultInstances);
        } else if (fieldValue instanceof Collection) {
            // If it's a list, we assume it's a list of IBeans
            Collection<AnchorBean<?>> valueCast = (Collection<AnchorBean<?>>) fieldValue;
            checkCollection(valueCast, field, beanName);
        } else if (fieldValue instanceof String) {
            String valueCast = (String) fieldValue;
            checkString(valueCast, field, beanName);
        } else {
            checkPropertyPrimitiveTypes(field, fieldValue, beanName);
        }
    }

    private void checkPropertyPrimitiveTypes(Field field, Object fieldValue, String beanName)
            throws BeanMisconfiguredException {
        if (fieldValue instanceof Integer) {
            Integer valueCast = (Integer) fieldValue;
            checkInteger(valueCast, field, beanName);
        } else if (fieldValue instanceof Double) {
            Double valueCast = (Double) fieldValue;
            checkDouble(valueCast, field, beanName);
        } else if (fieldValue instanceof Float) {
            Float valueCast = (Float) fieldValue;
            checkFloat(valueCast, field, beanName);
        } else if (fieldValue instanceof Short) {
            Short valueCast = (Short) fieldValue;
            checkShort(valueCast, field, beanName);
        } else if (fieldValue instanceof Long) {
            Long valueCast = (Long) fieldValue;
            checkLong(valueCast, field, beanName);
        }
    }

    private void checkCollection(Collection<AnchorBean<?>> list, Field field, String beanName)
            throws BeanMisconfiguredException {

        if (field.isAnnotationPresent(NonEmpty.class) && list.isEmpty()) {
            throw new BeanMisconfiguredException(
                    String.format(
                            "at least one item in property '%s' of '%s' must be present",
                            field.getName(), beanName));
        }

        for (AnchorBean<?> item : list) {
            if (item != null) {
                item.checkMisconfigured(defaultInstances);
            }
        }
    }

    private static void checkString(String fieldValue, Field field, String beanName)
            throws BeanMisconfiguredException {

        if (!field.isAnnotationPresent(AllowEmpty.class)) {
            if (fieldValue == null || fieldValue.isEmpty()) { // NOSONAR
                throw new BeanMisconfiguredException(
                        String.format(
                                "Property '%s' of '%s' must be non-empty",
                                field.getName(), beanName));
            }
        }
    }

    private static void generateNonNegativeException(String fieldName, String beanName)
            throws BeanMisconfiguredException {
        throw new BeanMisconfiguredException(
                String.format("Property '%s' of '%s' must be >= 0", fieldName, beanName));
    }

    private static void generatePositiveException(String fieldName, String beanName)
            throws BeanMisconfiguredException {
        throw new BeanMisconfiguredException(
                String.format("Property '%s' of '%s' must be > 0", fieldName, beanName));
    }

    private static void checkInteger(Integer fieldValue, Field field, String beanName)
            throws BeanMisconfiguredException {

        if (field.isAnnotationPresent(NonNegative.class) && fieldValue < 0) {
            generateNonNegativeException(field.getName(), beanName);
        }

        if (field.isAnnotationPresent(Positive.class) && fieldValue <= 0) {
            generatePositiveException(field.getName(), beanName);
        }
    }

    private static void checkShort(Short fieldValue, Field field, String beanName)
            throws BeanMisconfiguredException {

        if (field.isAnnotationPresent(NonNegative.class) && fieldValue < 0) {
            generateNonNegativeException(field.getName(), beanName);
        }

        if (field.isAnnotationPresent(Positive.class) && fieldValue <= 0) {
            generatePositiveException(field.getName(), beanName);
        }
    }

    private static void checkDouble(Double fieldValue, Field field, String beanName)
            throws BeanMisconfiguredException {

        if (field.isAnnotationPresent(NonNegative.class) && (fieldValue < 0)) {
            generateNonNegativeException(field.getName(), beanName);
        }

        if (field.isAnnotationPresent(Positive.class) && (fieldValue <= 0)) {
            generatePositiveException(field.getName(), beanName);
        }
    }

    private static void checkFloat(Float fieldValue, Field field, String beanName)
            throws BeanMisconfiguredException {

        if (field.isAnnotationPresent(NonNegative.class) && (fieldValue < 0)) {
            generateNonNegativeException(field.getName(), beanName);
        }

        if (field.isAnnotationPresent(Positive.class) && (fieldValue <= 0)) {
            generatePositiveException(field.getName(), beanName);
        }
    }

    private static void checkLong(Long fieldValue, Field field, String beanName)
            throws BeanMisconfiguredException {

        if (field.isAnnotationPresent(NonNegative.class) && (fieldValue < 0)) {
            generateNonNegativeException(field.getName(), beanName);
        }

        if (field.isAnnotationPresent(Positive.class) && (fieldValue <= 0)) {
            generatePositiveException(field.getName(), beanName);
        }
    }
}
