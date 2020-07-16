/* (C)2020 */
package org.anchoranalysis.image.bean.threshold.relation;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.relation.GreaterThan;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.image.binary.values.BinaryValues;

/**
 * Selects only the "high" pixels from a binary mask
 *
 * <p>Uses the default "high" value of 255
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public class BinaryHighVoxels extends BinaryVoxelsBase {

    @Override
    public double threshold() {
        return (double) (BinaryValues.getDefault().getOnInt() - 1);
    }

    @Override
    public RelationToValue relation() {
        return new GreaterThan();
    }
}
