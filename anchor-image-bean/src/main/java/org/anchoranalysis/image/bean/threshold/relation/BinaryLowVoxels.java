package org.anchoranalysis.image.bean.threshold.relation;

import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.relation.LessThan;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.image.binary.values.BinaryValues;

/**
 * Selects only the "low" pixels from a binary mask
 * 
 * <p>Uses the default "low" value of 0</p>
 * 
 * @author Owen Feehan
 *
 */
public class BinaryLowVoxels extends RelationToThreshold {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public double threshold() {
		return (double) (BinaryValues.getDefault().getOffInt() + 1);
	}

	@Override
	public RelationToValue relation() {
		return new LessThan();
	}
}
