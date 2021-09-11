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

package org.anchoranalysis.bean.permute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.exception.BeanDuplicateException;
import org.anchoranalysis.bean.permute.assign.AssignPermutationException;
import org.anchoranalysis.bean.permute.assign.PermutationAssigner;
import org.anchoranalysis.bean.permute.property.PermuteProperty;
import org.anchoranalysis.core.exception.CreateException;

/**
 * Applies a {@link PermuteProperty} to a bean to create new duplicated beans each with different
 * permuted values.
 *
 * @param <T> the type of bean which will be copied and permuted.
 * @author Owen Feehan
 */
@AllArgsConstructor
public class PermutedCopyCreator<T extends AnchorBean<T>> {

    /** Gets a name from object of type {@code T}. */
    private final Function<T, String> nameGetter;

    /** sets a name on an object of type {@code T}. */
    private final BiConsumer<T, String> nameSetter;

    /**
     * Applies a permutation to a copy of each element in a list of beans, updating the custom name
     * of each bean to reflect the permutation.
     *
     * <p>Specifically, {@code .XXX} is appended to each name, where {@code XXX} describes the
     * permutation.
     *
     * @param beans list of beans on which a permutation is applied.
     * @param propertyToPermute the property to permute, and all the values for the permutation.
     * @param setter for setting the permutation onto the property.
     * @param <S> permutation-type
     * @return a list containing the permuted beans.
     * @throws CreateException if something goes wrong.
     */
    public <S> List<T> createPermutedCopies(
            List<T> beans, PermuteProperty<S> propertyToPermute, PermutationAssigner setter)
            throws CreateException {

        List<T> out = new ArrayList<>();

        try {
            for (T bean : beans) {
                Iterator<S> valuesToPermute = propertyToPermute.propertyValues();

                if (!valuesToPermute.hasNext()) {
                    throw new CreateException("No values exist to assign during permutation.");
                }

                while (valuesToPermute.hasNext()) {
                    out.add(
                            copyAndAssignValue(
                                    bean, valuesToPermute.next(), propertyToPermute, setter));
                }
            }

            return out;

        } catch (BeanDuplicateException | IllegalArgumentException | AssignPermutationException e) {
            throw new CreateException(e);
        }
    }

    /**
     * Copies a bean, and assigns a value to the property (or properties) selected for permutation.
     */
    private <S> T copyAndAssignValue(
            T bean,
            S valueToAssign,
            PermuteProperty<S> propertyToPermute,
            PermutationAssigner setter)
            throws AssignPermutationException {
        T duplicated = bean.duplicateBean();
        setter.assignValue(duplicated, valueToAssign);
        assignUpdatedName(duplicated, propertyToPermute, valueToAssign);
        return duplicated;
    }

    private <S> void assignUpdatedName(T feature, PermuteProperty<S> permute, S propVal) {
        String nameNew = sensibleName(feature, permute, propVal);
        nameSetter.accept(feature, nameNew);
    }

    private <S> String sensibleName(T feature, PermuteProperty<S> permute, S propVal) {
        String toAppend = permute.describePropertyValue(propVal);

        String exsting = nameGetter.apply(feature);
        if (!exsting.isEmpty()) {
            // We update the custom name
            return String.format("%s.%s", exsting, toAppend);
        } else {
            // When there's no existing custom name (first PermuteProperty)
            return toAppend;
        }
    }
}
