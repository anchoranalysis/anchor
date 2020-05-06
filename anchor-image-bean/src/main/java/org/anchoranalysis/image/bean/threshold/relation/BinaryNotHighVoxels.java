package org.anchoranalysis.image.bean.threshold.relation;

import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;
import org.anchoranalysis.core.relation.LessThan;
import org.anchoranalysis.core.relation.RelationToValue;
import org.anchoranalysis.image.binary.values.BinaryValues;

/**
 * Selects anything that is NOT "high" pixels from a binary mask
 * 
 * <p>Uses the default "high" value of 255</p>
 * 
 * <p>Note this is not the same as selecting "low" pixels which would only select values of 0. There's fuzzy undefined space > 1 and < 255</p>.
 * 
 * @author Owen Feehan
 *
 */
public class BinaryNotHighVoxels extends RelationToThreshold {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public double threshold() {
		return BinaryValues.getDefault().getOnInt();
	}

	@Override
	public RelationToValue relation() {
		return new LessThan();
	}
}
