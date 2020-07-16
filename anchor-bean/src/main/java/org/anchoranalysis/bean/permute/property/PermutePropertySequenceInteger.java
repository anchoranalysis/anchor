/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;

/**
 * An arithmetic sequence of doubles, obtained by dividing an integer sequence by a divider
 *
 * @author Owen Feehan
 * @param T permutation type
 */
public class PermutePropertySequenceInteger extends PermutePropertySequence<Integer> {

    @Override
    public Iterator<Integer> propertyValues() {
        return propertyValuesDefinitelyInteger();
    }

    public Iterator<Integer> propertyValuesDefinitelyInteger() {
        return range();
    }

    @Override
    public String nameForPropValue(Integer value) {
        int numDigitsEnd = Integer.toString(getSequence().getEnd()).length();
        if (numDigitsEnd == 1) {
            return value.toString();
        } else {
            String format = String.format("%%0%dd", numDigitsEnd);
            return String.format(format, value);
        }
    }
}
