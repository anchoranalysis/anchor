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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.initializable.property.BeanInitializer;
import org.anchoranalysis.core.exception.InitializeException;
import org.anchoranalysis.core.log.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperInit {

    /**
     * Initializes the bean.
     *
     * @param bean bean to initialize.
     * @param initializer the property-initializer to use.
     * @param logger logger.
     * @throws InitializeException if errors occur initializing beans.
     */
    public static void initializeRecursive(
            AnchorBean<?> bean, BeanInitializer<?> initializer, Logger logger)
            throws InitializeException {

        List<BeanAndParent> everything = new LinkedList<>();
        everything.add(new BeanAndParent(bean, null));

        Set<AnchorBean<?>> done = new HashSet<>();

        while (!everything.isEmpty()) {

            BeanAndParent removedObj = everything.remove(everything.size() - 1);

            if (!done.contains(removedObj.getBean())) {
                maybeInitializeChildren(
                        removedObj, everything, bean.getBeanName(), initializer, logger);

                done.add(removedObj.getBean());
            }
        }
    }

    private static void maybeInitializeChildren(
            BeanAndParent bean,
            List<BeanAndParent> listInit,
            String beanNameFollowingFrom,
            BeanInitializer<?> propertyInitializer,
            Logger logger)
            throws InitializeException {

        try {
            boolean didInitChild =
                    propertyInitializer.applyInitializationIfPossibleTo(
                            bean.getBean(), bean.parentBean(), logger);
            if (didInitChild) {
                // Add children of the object, but only if PropertyInitializer initializes the
                // object
                FindChildrenForInit.addChildrenFromBean(bean, listInit);
            } else {
                throwExceptionIfInitializable(
                        bean.getBean(),
                        beanNameFollowingFrom,
                        propertyInitializer.getInitializationType().toString());
            }

        } catch (InitializeException e) {
            String msg =
                    String.format(
                            "Cannot initialize a field '%s' when initializing '%s'",
                            bean.pathFromRootAsString(), beanNameFollowingFrom);
            throw new InitializeException(msg, e);
        }
    }

    private static void throwExceptionIfInitializable(
            AnchorBean<?> bean, String beanNameFollowingFrom, String provider)
            throws InitializeException {

        if (bean instanceof InitializableBean) {
            InitializableBean<?, ?> beanCast = (InitializableBean<?, ?>) bean;
            throw new InitializeException(
                    String.format(
                            "Could not find matching parameters to initialize %s (%s) (recursively following from %s). Requires %s. Provider is %s.",
                            beanCast.getBeanName(),
                            beanCast.getClass().getName(),
                            beanNameFollowingFrom,
                            beanCast.getPropertyInitializer().describeAcceptedClasses(),
                            provider));
        }
    }
}
