/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.StringList;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.permute.setter.PermutationSetter;
import org.anchoranalysis.bean.permute.setter.PermutationSetterException;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Proxies an existing PermuteProperty but replaces the existing naming behaviour
 *
 * <p>Instead, names are determined by an integer lookup table.
 *
 * <p>TODO replace permuteProperty with an automatically generated sequence as it can be inferred
 * from the StringList
 *
 * @param <T> property type
 */
public class PermutePropertyDescribeIndex extends PermuteProperty<Integer> {

    // START BEAN FIELDS
    @BeanField @Getter @Setter private PermuteProperty<Integer> permuteProperty;

    @BeanField @Getter @Setter private StringList lookup;
    // END BEAN FIELDS

    @Override
    public Iterator<Integer> propertyValues() {
        return permuteProperty.propertyValues();
    }

    @Override
    public PermutationSetter createSetter(AnchorBean<?> parentBean)
            throws PermutationSetterException {
        return permuteProperty.createSetter(parentBean);
    }

    @Override
    public String nameForPropValue(Integer value) throws OperationFailedException {

        try {
            return lookup.list().get(value);

        } catch (IndexOutOfBoundsException e) {
            throw new OperationFailedException(
                    String.format("No lookup value found for index %d", value));
        }
    }
}
