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
package org.anchoranalysis.bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.error.BeanDuplicateException;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.apache.commons.lang3.ClassUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperDuplication {

    @SuppressWarnings("unchecked")
    public static <T> AnchorBean<T> duplicate(AnchorBean<T> bean) {

        try {
            AnchorBean<T> beanOut = bean.getClass().getConstructor().newInstance();

            for (Field field : bean.getOrCreateBeanFields()) {

                Optional<Object> propertyNew =
                        duplicatePropertyValue(
                                Optional.ofNullable(field.get(bean)),
                                field.getName(),
                                field.isAnnotationPresent(OptionalBean.class),
                                beanOut);
                if (propertyNew.isPresent()) {
                    field.set(beanOut, propertyNew.get());
                }
            }

            // We also copy the localization information for the new bean
            beanOut.localise(bean.getLocalPath());
            return beanOut;

        } catch (IllegalAccessException
                | IllegalArgumentException
                | InstantiationException
                | BeanMisconfiguredException
                | NoSuchMethodException
                | SecurityException
                | InvocationTargetException e) {
            throw new BeanDuplicateException(e);
        }
    }

    private static Object duplicateCollection(
            Collection<?> collection, String propertyName, AnchorBean<?> parentBean) {
        Stream<Optional<Object>> stream =
                collection.stream()
                        .map(
                                object ->
                                        duplicatePropertyValue(
                                                Optional.ofNullable(object),
                                                propertyName,
                                                false,
                                                parentBean));
        return stream.filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }

    private static Optional<Object> duplicatePropertyValue(
            Optional<Object> propertyValue,
            String propertyName,
            boolean optional,
            AnchorBean<?> parentBean) {

        if (!propertyValue.isPresent()) {
            if (optional) {
                return Optional.empty();
            } else {
                throw new BeanDuplicateException(
                        String.format(
                                "Property '%s' of '%s' is null, but non-optional",
                                propertyName, parentBean.getBeanName()));
            }
        }

        return duplicatePropertyValue(propertyValue.get(), propertyName, parentBean);
    }

    @SuppressWarnings("rawtypes")
    private static Optional<Object> duplicatePropertyValue(
            Object propertyValue, String propertyName, AnchorBean<?> parentBean) {
        if (propertyValue instanceof StringSet) {
            StringSet propertyValueCast = (StringSet) propertyValue;
            return Optional.of(propertyValueCast.duplicateBean());

        } else if (propertyValue instanceof AnchorBean) {
            // Our first priority is to duplicate a bean if we can, as it is possible for a Bean to
            // be a Collection as well, and it's better
            //  to use the Bean's normal duplication method
            AnchorBean propertyValueCast = (AnchorBean) propertyValue;
            return Optional.of(propertyValueCast.duplicateBean());

        } else if (propertyValue instanceof Collection) {
            // If it's a collection, then we do it item by item in the collection, and then exit
            return Optional.of(
                    duplicateCollection((Collection<?>) propertyValue, propertyName, parentBean));

        } else if (isImmutableType(propertyValue.getClass())) {
            // Any supported immutable type can be returned without duplication
            return Optional.of(propertyValue);

        } else {
            throw new BeanDuplicateException(
                    String.format(
                            "Unsupported property type: %s", propertyValue.getClass().toString()));
        }
    }

    /** Is a particular class what Java considered an immutable type? */
    private static boolean isImmutableType(Class<?> cls) {
        if (String.class.isAssignableFrom(cls)) {
            return true;
        } else if (Class.class.isAssignableFrom(cls)) {
            return true;
        } else {
            return ClassUtils.isPrimitiveOrWrapper(cls);
        }
    }
}
