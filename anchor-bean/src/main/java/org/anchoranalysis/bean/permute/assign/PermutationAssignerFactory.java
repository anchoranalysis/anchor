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

package org.anchoranalysis.bean.permute.assign;

import java.lang.reflect.Field;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;

/**
 * Creates instances of {@link PermutationAssigner}.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermutationAssignerFactory {

    /**
     * Creates an assigner for one or more properties, as identified by {@code propertyPath}.
     *
     * @param bean the bean to whom a permuted value will be assigned
     * @param propertyPath describes which property to change, either a simple property name {@code
     *     name1} or a nested chain {@code child1.child2.name1}.
     * @return an assigner for the permutation.
     * @throws AssignPermutationException if a matching field cannot be found.
     */
    public static PermutationAssigner createForSingle(AnchorBean<?> bean, String propertyPath)
            throws AssignPermutationException {

        // Get all the components of propertyPath by splitting by the period
        String[] tokens = propertyPath.split("\\.");

        if (tokens.length == 1) {
            // Simple case when there is a direct property
            Field finalField = findMatchingField(bean.fields(), propertyPath);
            return new SingleAssigner(finalField);
        } else {
            return createWithIntermediateProperties(bean, tokens);
        }
    }

    /**
     * Combines multiple existing assigners into a single {@link PermutationAssigner}s.
     *
     * @param assigners the assigners to combine
     * @return a new assigners that will assign to each entry in {@code assigners} through a unitary
     *     interface.
     */
    public static PermutationAssigner combine(List<PermutationAssigner> assigners) {
        return new ListAssigner(assigners);
    }

    private static Field findMatchingField(List<Field> list, String propertyName)
            throws AssignPermutationException {
        for (Field f : list) {
            if (f.getName().equals(propertyName)) {
                return f;
            }
        }
        throw new AssignPermutationException(
                String.format("Cannot find matching field for property '%s'", propertyName));
    }

    private static PermutationAssigner createWithIntermediateProperties(
            AnchorBean<?> parentBean, String[] tokens) throws AssignPermutationException {
        SingleAssigner setter = new SingleAssigner();

        AnchorBean<?> currentBean = parentBean;
        List<Field> currentList = currentBean.fields();

        int intermediateFields = tokens.length - 1;

        // We add each child along the way, keep the final field property
        for (int i = 0; i < intermediateFields; i++) {

            String currentPropertyName = tokens[i];

            // Find a matching property for the first child
            Field matchedField = findMatchingField(currentList, currentPropertyName);

            setter.addField(matchedField);

            currentBean = FindBean.beanFor(matchedField, currentBean);
            currentList = currentBean.fields();
        }

        // Find a matching property for the final setter
        setter.addField(findMatchingField(currentList, tokens[intermediateFields]));

        return setter;
    }
}
