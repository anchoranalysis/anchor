/* (C)2020 */
package org.anchoranalysis.bean.init;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.init.property.PropertyInitializer;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.log.Logger;

class HelperInit {

    private HelperInit() {}

    /**
     * Initializes the bean
     *
     * @param bean bean to initialise
     * @param pi the property-initializer to use
     * @param logger logger
     * @param parent an optional-bean parent to use, otherwise NULL
     * @throws InitException
     */
    public static void initRecursive(AnchorBean<?> bean, PropertyInitializer<?> pi, Logger logger)
            throws InitException {

        List<BeanAndParent> everything = new ArrayList<>();
        everything.add(new BeanAndParent(bean, null));

        Set<AnchorBean<?>> done = new HashSet<>();

        while (!everything.isEmpty()) {

            BeanAndParent removedObj = everything.remove(everything.size() - 1);

            if (done.contains(removedObj.getBean())) {
                continue;
            }

            maybeInitChildren(removedObj, everything, bean.getBeanName(), pi, logger);

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
