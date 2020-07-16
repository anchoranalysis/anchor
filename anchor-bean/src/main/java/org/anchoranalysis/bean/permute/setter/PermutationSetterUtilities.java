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

package org.anchoranalysis.bean.permute.setter;

import java.lang.reflect.Field;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermutationSetterUtilities {

    /**
     * Searches through a list of property fields to find one that matches the propertyName
     *
     * @throws PermutationSetterException
     */
    public static PermutationSetter createForSingle(AnchorBean<?> parentBean, String propertyPath)
            throws PermutationSetterException {

        // Get all the components of propertyPath
        String[] tokens = propertyPath.split("\\.");

        if (tokens.length == 1) {
            // Simple case when there is a direct property
            Field finalField = findMatchingField(parentBean.getOrCreateBeanFields(), propertyPath);
            return new PermutationSetterSingle(finalField);
        } else {
            return createWithIntermediateProperties(parentBean, tokens);
        }
    }

    /**
     * Finds a bean corresponding to a field (exceptionally handling a List by taking the first item
     * in it)
     *
     * @param field a field corresponding to an AnchorBean or a {@param java.util.List}
     * @param currentBean the bean object to which the field refers to
     * @return an AnchorBean corresponding to this particular field on this particular object
     *     (exceptionally handling lists)
     */
    public static AnchorBean<?> beanFor(Field field, AnchorBean<?> currentBean) // NOSONAR
            throws PermutationSetterException {

        try {
            Object currentObj = field.get(currentBean);

            if (currentObj instanceof AnchorBean) {
                return (AnchorBean<?>) currentObj;
            } else if (currentObj instanceof List) {
                List<?> list = ((List<?>) currentObj);
                return (AnchorBean<?>) list.get(0);
            } else {
                throw new PermutationSetterException(
                        "A field must be an Anchor-Bean or a List of Anchor-Beans. No other types are supported");
            }

        } catch (IllegalAccessException e) {
            throw new PermutationSetterException(
                    String.format(
                            "An illegal access exception occurred accessing field %s", field));
        }
    }

    private static Field findMatchingField(List<Field> list, String propertyName)
            throws PermutationSetterException {
        for (Field f : list) {
            if (f.getName().equals(propertyName)) {
                return f;
            }
        }
        throw new PermutationSetterException(
                String.format("Cannot find matching field for property '%s'", propertyName));
    }

    private static PermutationSetter createWithIntermediateProperties(
            AnchorBean<?> parentBean, String[] tokens) throws PermutationSetterException {
        PermutationSetterSingle setter = new PermutationSetterSingle();

        AnchorBean<?> currentBean = parentBean;
        List<Field> currentList = currentBean.getOrCreateBeanFields();

        int intermediateFields = tokens.length - 1;

        // We add each child along the way, keep the final field property
        for (int i = 0; i < intermediateFields; i++) {

            String currentPropertyName = tokens[i];

            // Find a matching property for the first child
            Field matchedField = findMatchingField(currentList, currentPropertyName);

            setter.addField(matchedField);

            currentBean = PermutationSetterUtilities.beanFor(matchedField, currentBean);
            currentList = currentBean.getOrCreateBeanFields();
        }

        // Find a matching property for the final setter
        setter.addField(findMatchingField(currentList, tokens[intermediateFields]));

        return setter;
    }
}
