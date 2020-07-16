/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;

/**
 * Base class for permute-properties involving a sequence of numbers
 *
 * @author Owen Feehan
 * @param <T>
 */
public abstract class PermutePropertySequence<T> extends PermutePropertyWithPath<T> {

    // START BEAN PROPERTIES
    /** Where the sequence starts */
    @BeanField @Getter @Setter private SequenceInteger sequence;
    // END BEAN PROPERTIES

    protected SequenceIntegerIterator range() {
        return sequence.iterator();
    }
}
