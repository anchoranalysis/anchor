/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.permute.setter.PermutationSetter;
import org.anchoranalysis.bean.permute.setter.PermutationSetterException;
import org.anchoranalysis.core.error.OperationFailedException;

/** @param <T> Type of permutations */
public abstract class PermuteProperty<T> extends AnchorBean<PermuteProperty<T>> {

    /**
     * Converts a particular value to a string representation
     *
     * @param value
     */
    public abstract String nameForPropValue(T value) throws OperationFailedException;

    public abstract PermutationSetter createSetter(AnchorBean<?> parentBean)
            throws PermutationSetterException;

    /**
     * An iterator (of size numPermutations()) with the values for the property
     *
     * @return a new iterator for the permutations that are possible
     */
    public abstract Iterator<T> propertyValues();
}
