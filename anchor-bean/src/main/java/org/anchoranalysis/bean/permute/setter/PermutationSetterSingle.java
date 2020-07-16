/* (C)2020 */
package org.anchoranalysis.bean.permute.setter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.bean.AnchorBean;

/**
 * Sets a permutation from a list of fields
 *
 * <p><div> All fields must refer to either:
 * <li>
 *
 *     <ol>
 *       An {@link AnchorBean}
 * </ol>
 *
 * <ol>
 *   (as an exception) A {@link java.util.List} of containing at least one item of type {@link
 *   AnchorBean}. The first item of the list will be taken as the corresponding Anchor-Bean. </div>
 *
 * @author Owen Feehan
 */
public class PermutationSetterSingle implements PermutationSetter {

    /** Intermediate properties that must be traversed before getting to the final field */
    private List<Field> listFields = new ArrayList<>();

    public PermutationSetterSingle() {
        // Nothing is initialized, one must do it themselves
    }

    public PermutationSetterSingle(Field field) {
        super();
        addField(field);
    }

    public void addField(Field field) {
        this.listFields.add(field);
    }

    @Override
    public void setPermutation(AnchorBean<?> bean, Object val) throws PermutationSetterException {

        try {
            int numIntermediate = listFields.size() - 1;

            // We iterate through all the intermediate fields
            AnchorBean<?> currentBean = bean;
            for (int i = 0; i < numIntermediate; i++) {
                Field p = listFields.get(i);

                currentBean = PermutationSetterUtilities.beanFor(p, currentBean);
            }

            // Now do the final field, where we actually set the value
            Field finalField = listFields.get(numIntermediate);

            Object valMaybeConverted = maybeConvert(val, finalField.getType());
            finalField.set(currentBean, valMaybeConverted);

        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new PermutationSetterException(
                    "An illegal argument or access exception occurred setting a permutation on the bean");
        }
    }

    /**
     * We hardcore special cases, where the object conversion occurs.
     *
     * <p>1. From any object to a string.
     *
     * @return
     */
    private static Object maybeConvert(Object val, Class<?> targetType) {

        if (targetType.equals(String.class)) {
            return val.toString();
        }
        return val;
    }
}
