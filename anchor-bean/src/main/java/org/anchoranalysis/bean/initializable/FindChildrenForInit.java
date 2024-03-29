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

package org.anchoranalysis.bean.initializable;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.FieldAccessor;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.core.exception.InitializeException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FindChildrenForInit {

    /**
     * Finds children of bean that need to be initialized - and adds them to a list.
     *
     * @param bean the bean whose children will be searched
     * @param toAddTo a list containing all the children of the bean that should be initialized
     * @throws InitializeException if any problems occurring access fields in the bean using
     *     reflection.
     */
    public static void addChildrenFromBean(BeanAndParent bean, List<BeanAndParent> toAddTo)
            throws InitializeException {

        List<Field> beanFields = bean.getBean().fields();

        for (Field field : beanFields) {

            if (!field.isAnnotationPresent(SkipInit.class)) {

                // Create a FIFO queue of items to be initialized
                addChildrenMany(
                        toAddTo, bean, field, FieldAccessor.isFieldAnnotatedAsOptional(field));
            }
        }
    }

    private static void addChildrenMany(
            List<BeanAndParent> listOut, BeanAndParent bean, Field field, boolean optional)
            throws InitializeException {

        try {
            Object propertyValue = field.get(bean.getBean());

            // If it's a collection, then we do it item by item in the collection, and then exit
            if (propertyValue instanceof Collection) {

                Collection<?> collection = (Collection<?>) propertyValue;
                for (Object object : collection) {
                    addChildren(listOut, bean, object, false);
                }

                return;
            }

            addChildren(listOut, bean, propertyValue, optional);

        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new InitializeException(e);
        }
    }

    private static void addChildren(
            List<BeanAndParent> listOut,
            BeanAndParent bean,
            Object propertyValue,
            boolean optional) {

        // If it's an optional attribute we check that it's not null
        if (optional && propertyValue == null) {
            return;
        }

        // We only add if its a bean itself
        if (propertyValue instanceof AnchorBean) {
            listOut.add(new BeanAndParent((AnchorBean<?>) propertyValue, bean));
        }
    }
}
