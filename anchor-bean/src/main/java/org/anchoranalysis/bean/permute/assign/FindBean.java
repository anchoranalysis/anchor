/*-
 * #%L
 * anchor-bean
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
import lombok.AllArgsConstructor;
import org.anchoranalysis.bean.AnchorBean;

/**
 * Helps find a bean corresponding to a {@link Field}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class FindBean {

    /**
     * Finds a bean corresponding to a field.
     *
     * <p>Exceptionally, it can also accept a {@link List} of beans instead of a single bean, by
     * simply taking the first item from the list.
     *
     * @param field a field corresponding to an AnchorBean or a {@link java.util.List}
     * @param currentBean the bean object to which the field refers to
     * @return an AnchorBean corresponding to this particular field on this particular object
     *     (exceptionally handling lists)
     * @throws AssignPermutationException if {@code field} is of incorrect types, or cannot be
     *     legally accessed.
     */
    public static AnchorBean<?> beanFor(Field field, AnchorBean<?> currentBean) // NOSONAR
            throws AssignPermutationException {

        try {
            Object currentObj = field.get(currentBean);

            if (currentObj instanceof AnchorBean) {
                return (AnchorBean<?>) currentObj;
            } else if (currentObj instanceof List) {
                List<?> list = ((List<?>) currentObj);
                return (AnchorBean<?>) list.get(0);
            } else {
                throw new AssignPermutationException(
                        "A field must be an Anchor-Bean or a List of Anchor-Beans. No other types are supported");
            }

        } catch (IllegalAccessException e) {
            throw new AssignPermutationException(
                    String.format(
                            "An illegal access exception occurred accessing field %s", field));
        }
    }
}
