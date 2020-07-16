/* (C)2020 */
package org.anchoranalysis.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HelperReflection {

    public static PropertyDescriptor[] findAllPropertyDescriptors(Class<?> beanClass)
            throws IntrospectionException {
        BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, Object.class);
        return beanInfo.getPropertyDescriptors();
    }

    /**
     * Finds all fields, including fields in parents in the inheritance tree.
     *
     * @param classWithFields class with fields
     * @return all fields in the class
     */
    public static List<Field> findAllFields(Class<?> classWithFields) {

        List<Field> out = new ArrayList<>();

        Class<?> current = classWithFields;
        do {
            for (Field f : current.getDeclaredFields()) {
                out.add(f);
            }
        } while ((current = current.getSuperclass()) != null);

        return out;
    }
}
