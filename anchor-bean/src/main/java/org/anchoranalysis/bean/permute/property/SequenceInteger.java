/* (C)2020 */
package org.anchoranalysis.bean.permute.property;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.BeanInstanceMap;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.error.BeanMisconfiguredException;

public class SequenceInteger extends AnchorBean<SequenceInteger> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int start = 0;

    // Last item, inclusive
    @BeanField @Getter @Setter private int end = 0;

    @BeanField @Getter @Setter private int increment = 1;
    // END BEAN PROPERTIES

    @Override
    public void checkMisconfigured(BeanInstanceMap defaultInstances)
            throws BeanMisconfiguredException {
        super.checkMisconfigured(defaultInstances);
        if (end < start) {
            throw new BeanMisconfiguredException(
                    String.format("end (%d) cannot be less than start (%d)", start, end));
        }
    }

    public SequenceIntegerIterator iterator() {
        return new SequenceIntegerIterator(start, end, increment);
    }
}
