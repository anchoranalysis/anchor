/* (C)2020 */
package org.anchoranalysis.image.bean.threshold.relation;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.relation.LessThan;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.image.binary.values.BinaryValues;

/**
 * Selects only the "low" pixels from a binary mask
 *
 * <p>Uses the default "low" value of 0
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public class BinaryLowVoxels extends BinaryVoxelsBase {

    @Override
    public double threshold() {
        return (double) (BinaryValues.getDefault().getOffInt() + 1);
    }

    @Override
    public RelationToValue relation() {
        return new LessThan();
    }
}
