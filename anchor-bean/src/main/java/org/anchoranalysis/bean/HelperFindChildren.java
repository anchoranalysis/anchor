/* (C)2020 */
package org.anchoranalysis.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;
import org.anchoranalysis.bean.error.BeanStrangeException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperFindChildren {

    /**
     * Finds all bean-fields that are instance of a certain class. All immediate children are
     * checked, and any items in immediate lists.
     *
     * @param bean the bean whose fields will be searched
     * @param listFields the list of fields associated with the bean
     * @param match the class that a field must be assignable from (equal to or inherit from)
     * @return
     * @throws BeanMisconfiguredException
     */
    public static <T extends AnchorBean<?>> List<T> findChildrenOfClass(
            AnchorBean<?> bean, List<Field> listFields, Class<?> match)
            throws BeanMisconfiguredException {

        List<T> out = new ArrayList<>();

        try {
            for (Field field : listFields) {
                Object value = field.get(bean);

                // If it's non-optional, then we insist it's non-null
                if (value == null) {
                    if (field.isAnnotationPresent(OptionalBean.class)) {
                        continue;
                    } else {
                        HelperBeanFields.throwMissingPropertyException(
                                field.getName(), bean.getBeanName());
                    }
                }

                maybeAdd(value, match, out);
            }
        } catch (IllegalAccessException e) {
            throw new BeanStrangeException(
                    "While using reflection, a permissions problem occurred", e);
        }

        return out;
    }

    /**
     * Adds an object to a list, only if it matches our condition.
     *
     * @param fieldValue the object to consider adding
     * @param match the class that a field must be assignable from (equal to or inherit from)
     * @param out a list of objects that matched
     */
    @SuppressWarnings("unchecked")
    private static <T extends AnchorBean<?>> void maybeAdd(
            Object fieldValue, Class<?> match, List<T> out) {

        if (fieldValue instanceof AnchorBean) {
            AnchorBean<?> valueCast = (AnchorBean<?>) fieldValue;

            if (match.isAssignableFrom(valueCast.getClass())) {
                out.add((T) valueCast);
            }
        }

        // If it's a list, we assume it's a list of IBeans
        if (fieldValue instanceof Collection) {
            Collection<AnchorBean<?>> valueCast = (Collection<AnchorBean<?>>) fieldValue;
            maybeAddCollection(valueCast, match, out);
        }
    }

    /**
     * Adds an object to a list, only if it matches our condition.
     *
     * @param list considers all objects in this list
     * @param match the class to be matched
     * @param out out a list of objects that matched
     */
    @SuppressWarnings("unchecked")
    private static <T extends AnchorBean<?>> void maybeAddCollection(
            Collection<AnchorBean<?>> list, Class<?> match, List<T> out) {

        for (AnchorBean<?> item : list) {
            if (item != null && match.isAssignableFrom(item.getClass())) {
                out.add((T) item);
            }
        }
    }
}
