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

package org.anchoranalysis.bean.init;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperInit {

    /**
     * Initializes the bean
     *
     * @param bean bean to initialise
     * @param initializer the property-initializer to use
     * @param logger logger
     * @throws InitException
     */
    public static void initRecursive(AnchorBean<?> bean, PropertyInitializer<?> initializer, Logger logger)
            throws InitException {

        List<BeanAndParent> everything = new ArrayList<>();
        everything.add(new BeanAndParent(bean, null));

        Set<AnchorBean<?>> done = new HashSet<>();

        while (!everything.isEmpty()) {

            BeanAndParent removedObj = everything.remove(everything.size() - 1);

            if (done.contains(removedObj.getBean())) {
                continue;
            }

            maybeInitChildren(removedObj, everything, bean.getBeanName(), initializer, logger);

            done.add(removedObj.getBean());
        }
    }

    private static void maybeInitChildren(
            BeanAndParent bean,
            List<BeanAndParent> listInit,
            String beanNameFollowingFrom,
            PropertyInitializer<?> pi,
            Logger logger)
            throws InitException {

        try {
            boolean didInitChild =
                    pi.applyInitializationIfPossibleTo(bean.getBean(), bean.parentBean(), logger);
            if (didInitChild) {
                // Add children of the object, but only if PropertyInitializer initializes the
                // object
                FindChildrenForInit.addChildrenFromBean(bean, listInit);
            } else {
                throwExceptionIfInitializable(
                        bean.getBean(), beanNameFollowingFrom, pi.getInitParamType().toString());
            }

        } catch (InitException e) {
            String msg =
                    String.format(
                            "Cannot initialize a field '%s' when initializing '%s'",
                            bean.pathFromRootAsString(), beanNameFollowingFrom);
            throw new InitException(msg, e);
        }
    }

    private static void throwExceptionIfInitializable(
            AnchorBean<?> bean, String beanNameFollowingFrom, String provider)
            throws InitException {

        if (bean instanceof InitializableBean) {
            InitializableBean<?, ?> objCast = (InitializableBean<?, ?>) bean;
            throw new InitException(
                    String.format(
                            "Could not find matching params to initialise %s (%s) (recursively following from %s). Requires %s. Provider is %s.",
                            objCast.getBeanName(),
                            objCast.getClass().getName(),
                            beanNameFollowingFrom,
                            objCast.getPropertyDefiner().describeAcceptedClasses(),
                            provider));
        }
    }
}
