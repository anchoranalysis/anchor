package org.anchoranalysis.bean;

import java.lang.reflect.Field;
import java.util.Optional;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.exception.BeanMisconfiguredException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utilities to access a {@link Field} of an {@link AnchorBean}.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class FieldAccessor {
    
    /**
     * Retrieves the value of a {@link Field} from a {@link AnchorBean}.
     * 
     * @param bean the bean
     * @param field the field to retrieve a value from
     * @return the value, it it exists, or {@code Optional.empty()} if it doesn't exist <b>and</b> the bean is marked as optional.
     * @throws IllegalAccessException
     * @throws BeanMisconfiguredException if no value exists for the field (and it's not marked as optional).
     */
    public static Optional<Object> fieldFromBean(AnchorBean<?> bean, Field field) throws IllegalAccessException, BeanMisconfiguredException {
        // If it's non-optional, then we insist it's non-null
        Object value = field.get(bean);
        if (value == null) {
            if (isFieldAnnotatedAsOptional(field)) {
                return Optional.empty();
            } else {
                throwMissingPropertyException(field.getName(), bean.getBeanName());
            }
        }
        return Optional.of(value);
    }
    
    /**
     * Is a particular {@link Field} on a {@link AnchorBean} marked as optional?
     * 
     * @param field the field
     * @return true iff it is marked as optional.
     */
    public static boolean isFieldAnnotatedAsOptional(Field field) {
        return field.isAnnotationPresent(OptionalBean.class);
    }
    
    private static void throwMissingPropertyException(String fieldName, String className)
            throws BeanMisconfiguredException {
        throw new BeanMisconfiguredException(
                String.format("Property %s of class %s must be non-null", fieldName, className));
    }
}
