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
import java.util.Optional;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.DefaultInstance;
import org.anchoranalysis.bean.annotation.NonEmpty;
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.bean.annotation.Positive;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;

class HelperCheckMisconfigured {

    /**
     * A map between class-types and bean-instances used to populate default-values for beans with
     * certain annotations.
     */
    private final BeanInstanceMap defaultInstances;

    /**
     * Creates a new instance of {@link HelperCheckMisconfigured}.
     *
     * @param defaultInstances a map of default instances for beans
     */
    HelperCheckMisconfigured(final BeanInstanceMap defaultInstances) {
        this.defaultInstances = defaultInstances;
    }

    /**
     * Checks that a bean's properties are correct, throwing an exception if any bean is
     * misconfigured.
     *
     * <p>(e.g. that required fields are present, values match BeanField annotations etc.)
     *
     * <p>Note that a {@link BeanMisconfiguredException} does not indicate XML errors, rather
     * missing required properties etc.
     *
     * @param bean bean-object
     * @param listFields fields associated with the bean
     * @throws BeanMisconfiguredException if any of beans have incorrect configuration.
     */
    public void checkMisconfiguredWithFields(AnchorBean<?> bean, List<Field> listFields)
            throws BeanMisconfiguredException {

        String beanName = bean.getClass().getName();
        try {
            for (Field field : listFields) {

                Object value = field.get(bean);

                if (value == null && field.isAnnotationPresent(DefaultInstance.class)) {
                    // If there's no value set, and there's a DefaultInstance annotation, we search
                    // for
                    //  a defaultInstance and throw an error if it doesn't exist
                    value = findDefaultInstance(field.getType());
                    field.set(bean, value); // NOSONAR
                }

                // If it's non-optional, then we insist it's non-null
                if (value == null && !FieldAccessor.isFieldAnnotatedAsOptional(field)) {
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
     * Searches for an instance to use as a default for a class.
     *
     * <p>If no default can be found in the map, an exception is thrown.
     *
     * @param classType the type of the class to find a default for
     * @return the default-instance associated with the class. Null is not possible.
     * @throws BeanMisconfiguredException if no matching default can be found
     */
    private Object findDefaultInstance(Class<?> classType) throws BeanMisconfiguredException {

        Optional<Object> object = defaultInstances.getInstanceFor(classType);

        if (!object.isPresent()) {
            throw new BeanMisconfiguredException(
                    String.format(
                            "No default property can be found for class '%s'",
                            classType.getName()));
        }

        return object.get();
    }

    /**
     * Checks the property of a bean field.
     *
     * @param field the {@link Field} to check
     * @param fieldValue the value of the field
     * @param beanName the name of the bean
     * @throws BeanMisconfiguredException if the property is misconfigured
     */
    @SuppressWarnings("unchecked")
    private void checkProperty(Field field, Object fieldValue, String beanName)
            throws BeanMisconfiguredException {
        switch (fieldValue) {
            case AnchorBean<?> valueCast -> valueCast.checkMisconfigured(defaultInstances);
            case Collection<?> valueCast ->
                    checkCollection((Collection<AnchorBean<?>>) valueCast, field, beanName);
            case String valueCast -> checkString(valueCast, field, beanName);
            default -> checkPropertyPrimitiveTypes(field, fieldValue, beanName);
        }
    }

    /**
     * Checks primitive type properties of a bean field.
     *
     * @param field the {@link Field} to check
     * @param fieldValue the value of the field
     * @param beanName the name of the bean
     * @throws BeanMisconfiguredException if the property is misconfigured
     */
    private void checkPropertyPrimitiveTypes(Field field, Object fieldValue, String beanName)
            throws BeanMisconfiguredException {
        switch (fieldValue) {
            case Integer valueCast -> checkInteger(valueCast, field, beanName);
            case Double valueCast -> checkDouble(valueCast, field, beanName);
            case Float valueCast -> checkFloat(valueCast, field, beanName);
            case Short valueCast -> checkShort(valueCast, field, beanName);
            case Long valueCast -> checkLong(valueCast, field, beanName);
            default -> {} // No action for other types
        }
    }

    /**
     * Checks a collection property of a bean field.
     *
     * @param list the {@link Collection} to check
     * @param field the {@link Field} to check
     * @param beanName the name of the bean
     * @throws BeanMisconfiguredException if the collection is misconfigured
     */
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

    /**
     * Checks a string property of a bean field.
     *
     * @param fieldValue the string value to check
     * @param field the {@link Field} to check
     * @param beanName the name of the bean
     * @throws BeanMisconfiguredException if the string is misconfigured
     */
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

    /**
     * Generates an exception for a non-negative constraint violation.
     *
     * @param fieldName the name of the field
     * @param beanName the name of the bean
     * @throws BeanMisconfiguredException with a message about the non-negative constraint
     */
    private static void generateNonNegativeException(String fieldName, String beanName)
            throws BeanMisconfiguredException {
        throw new BeanMisconfiguredException(
                String.format("Property '%s' of '%s' must be >= 0", fieldName, beanName));
    }

    /**
     * Generates an exception for a positive constraint violation.
     *
     * @param fieldName the name of the field
     * @param beanName the name of the bean
     * @throws BeanMisconfiguredException with a message about the positive constraint
     */
    private static void generatePositiveException(String fieldName, String beanName)
            throws BeanMisconfiguredException {
        throw new BeanMisconfiguredException(
                String.format("Property '%s' of '%s' must be > 0", fieldName, beanName));
    }

    /**
     * Checks an integer property of a bean field.
     *
     * @param fieldValue the integer value to check
     * @param field the {@link Field} to check
     * @param beanName the name of the bean
     * @throws BeanMisconfiguredException if the integer is misconfigured
     */
    private static void checkInteger(Integer fieldValue, Field field, String beanName)
            throws BeanMisconfiguredException {

        if (field.isAnnotationPresent(NonNegative.class) && fieldValue < 0) {
            generateNonNegativeException(field.getName(), beanName);
        }

        if (field.isAnnotationPresent(Positive.class) && fieldValue <= 0) {
            generatePositiveException(field.getName(), beanName);
        }
    }

    /**
     * Checks a short property of a bean field.
     *
     * @param fieldValue the short value to check
     * @param field the {@link Field} to check
     * @param beanName the name of the bean
     * @throws BeanMisconfiguredException if the short is misconfigured
     */
    private static void checkShort(Short fieldValue, Field field, String beanName)
            throws BeanMisconfiguredException {

        if (field.isAnnotationPresent(NonNegative.class) && fieldValue < 0) {
            generateNonNegativeException(field.getName(), beanName);
        }

        if (field.isAnnotationPresent(Positive.class) && fieldValue <= 0) {
            generatePositiveException(field.getName(), beanName);
        }
    }

    /**
     * Checks a double property of a bean field.
     *
     * @param fieldValue the double value to check
     * @param field the {@link Field} to check
     * @param beanName the name of the bean
     * @throws BeanMisconfiguredException if the double is misconfigured
     */
    private static void checkDouble(Double fieldValue, Field field, String beanName)
            throws BeanMisconfiguredException {

        if (field.isAnnotationPresent(NonNegative.class) && (fieldValue < 0)) {
            generateNonNegativeException(field.getName(), beanName);
        }

        if (field.isAnnotationPresent(Positive.class) && (fieldValue <= 0)) {
            generatePositiveException(field.getName(), beanName);
        }
    }

    /**
     * Checks a float property of a bean field.
     *
     * @param fieldValue the float value to check
     * @param field the {@link Field} to check
     * @param beanName the name of the bean
     * @throws BeanMisconfiguredException if the float is misconfigured
     */
    private static void checkFloat(Float fieldValue, Field field, String beanName)
            throws BeanMisconfiguredException {

        if (field.isAnnotationPresent(NonNegative.class) && (fieldValue < 0)) {
            generateNonNegativeException(field.getName(), beanName);
        }

        if (field.isAnnotationPresent(Positive.class) && (fieldValue <= 0)) {
            generatePositiveException(field.getName(), beanName);
        }
    }

    /**
     * Checks a long property of a bean field.
     *
     * @param fieldValue the long value to check
     * @param field the {@link Field} to check
     * @param beanName the name of the bean
     * @throws BeanMisconfiguredException if the long is misconfigured
     */
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
