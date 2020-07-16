/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import java.util.Iterator;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

/**
 * An arithmetic sequence of doubles, obtained by dividing an integer sequence by a divider
 *
 * @author Owen Feehan
 */
public class PermutePropertySequenceDouble extends PermutePropertySequence<Double> {

    // START BEAN PROPERTIES
    /** Divide by */
    @BeanField @Getter @Setter private double divisor = 1.0;

    @Override
    public Iterator<Double> propertyValues() {
        return new DoubleRange();
    }
    // END BEAN PROPERTIES

    // http://stackoverflow.com/questions/371026/shortest-way-to-get-an-iterator-over-a-range-of-integers-in-java

    public class DoubleRange implements Iterator<Double> {
        private Iterator<Integer> itr;

        public DoubleRange() {
            itr = range();
        }

        @Override
        public boolean hasNext() {
            return itr.hasNext();
        }

        @Override
        public Double next() {
            return itr.next() / divisor;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String nameForPropValue(Double value) {
        return value.toString();
    }
}
