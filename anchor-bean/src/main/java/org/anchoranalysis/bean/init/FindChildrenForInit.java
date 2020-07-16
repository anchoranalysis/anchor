/* (C)2020 */
package org.anchoranalysis.bean.init;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.annotation.SkipInit;
import org.anchoranalysis.core.error.InitException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class FindChildrenForInit {

    /**
     * Finds children of bean that need to be initialized - and adds them to a list
     *
     * @param bean the bean whose children will be searched
     * @param listOut a list containing all the children of the bean that should be initialized
     * @throws InitException
     */
    public static void addChildrenFromBean(BeanAndParent bean, List<BeanAndParent> listOut)
            throws InitException {

        List<Field> beanFields = bean.getBean().getOrCreateBeanFields();

        for (Field field : beanFields) {

            if (!field.isAnnotationPresent(SkipInit.class)) {

                // Create a FIFO queue of items to be initialised
                addChildrenMany(
                        listOut, bean, field, field.isAnnotationPresent(OptionalBean.class));
            }
        }
    }

    private static void addChildrenMany(
            List<BeanAndParent> listOut, BeanAndParent bean, Field field, boolean optional)
            throws InitException {

        try {
            Object propertyValue = field.get(bean.getBean());

            // If it's a collection, then we do it item by item in the collection, and then exit
            if (propertyValue instanceof Collection) {

                Collection<?> collection = (Collection<?>) propertyValue;
                for (Object o : collection) {
                    addChildren(listOut, bean, o, false);
                }

                return;
            }

            addChildren(listOut, bean, propertyValue, optional);

        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new InitException(e);
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
