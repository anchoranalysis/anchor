/* (C)2020 */
package org.anchoranalysis.image.bean.threshold.relation;

import lombok.EqualsAndHashCode;
import org.anchoranalysis.core.relation.GreaterThan;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.image.binary.values.BinaryValues;

/**
 * Selects anything that is NOT "low" pixels from a binary mask
 *
 * <p>Uses the default "low" value of 255
 *
 * <p>Note this is not the same as selecting "high" pixels which would only select pixels of value
 * 255. There's fuzzy undefined space > 1 and < 255.
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode(callSuper = true)
public class BinaryNotLowVoxels extends BinaryVoxelsBase {

    @Override
    public double threshold() {
        return BinaryValues.getDefault().getOffInt();
    }

    @Override
    public RelationToValue relation() {
        return new GreaterThan();
    }
}
