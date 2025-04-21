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

package org.anchoranalysis.bean.permute.property;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.OptionalBean;
import org.anchoranalysis.bean.permute.assign.AssignPermutationException;
import org.anchoranalysis.bean.permute.assign.PermutationAssigner;
import org.anchoranalysis.bean.permute.assign.PermutationAssignerFactory;
import org.anchoranalysis.bean.primitive.StringSet;

/**
 * Changes properties of an {@link AnchorBean} to one of a range of possible values.
 *
 * <p>A property can be a direct property of the bean, or one or more indirect properties found in
 * nested child-beans.
 *
 * @param <T> element-type of the bean whose property or properties will be changed during
 *     permutation.
 */
public abstract class PermuteProperty<T> extends AnchorBean<PermuteProperty<T>> {

    // START BEAN PROPERTIES
    /**
     * Either a direct property of a bean <b>or</b> a nested-property with the children separated by
     * full-stops.
     *
     * <p>e.g. a direct property {@code someproperty} or a nested-property e.g. {@code
     * somechild1.somechild2.someproperty}
     *
     * <p>A child bean used in this way must be of single multiplicity, and always be a subclass of
     * {@link org.anchoranalysis.bean.AnchorBean}.
     */
    @BeanField @Getter @Setter private String propertyPath;

    /** Additional property paths that are also changed, together with {@code propertyPath}. */
    @BeanField @OptionalBean @Getter @Setter private StringSet additionalPropertyPaths;

    // END BEAN PROPERTIES

    /**
     * Describes a particular property-value in a unique but human-readable way.
     *
     * @param value the value to describe, which should one of the values returned by {@link
     *     #propertyValues()}.
     * @return the description of {@code value}.
     */
    public abstract String describePropertyValue(T value);

    /**
     * Creates a {@link PermutationAssigner} which allows the particular property to be changed.
     *
     * @param bean the bean whose property will be changed.
     * @return the setter that can change the property.
     * @throws AssignPermutationException if no property can be found at {@code propertyPath} to
     *     change.
     */
    public PermutationAssigner createSetter(AnchorBean<?> bean) throws AssignPermutationException {

        List<PermutationAssigner> assigners = new ArrayList<>();

        addFromPath(assigners, bean, propertyPath);

        if (additionalPropertyPaths != null) {
            addFromPathSet(assigners, bean, additionalPropertyPaths.set());
        }

        return PermutationAssignerFactory.combine(assigners);
    }

    /**
     * An iterator with possible alternative values for the property.
     *
     * @return a newly created iterator of all possible alternative values.
     */
    public abstract Iterator<T> propertyValues();

    private void addFromPathSet(
            List<PermutationAssigner> assigners, AnchorBean<?> parentBean, Set<String> paths)
            throws AssignPermutationException {
        for (String additionalPath : paths) {
            addFromPath(assigners, parentBean, additionalPath);
        }
    }

    private void addFromPath(
            List<PermutationAssigner> assigners, AnchorBean<?> parentBean, String path)
            throws AssignPermutationException {
        assigners.add(PermutationAssignerFactory.createForSingle(parentBean, path));
    }
}
