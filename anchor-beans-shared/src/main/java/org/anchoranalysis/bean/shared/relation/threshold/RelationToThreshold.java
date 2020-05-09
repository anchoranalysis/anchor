package org.anchoranalysis.bean.shared.relation.threshold;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.GenerateUniqueParameterization;
import org.anchoranalysis.core.relation.RelationToValue;

/**
 * A threshold and a relation to it, allowing for tests of a value in relation to a threshold.
 * 
 * @author Owen Feehan
 *
 */
public abstract class RelationToThreshold extends AnchorBean<RelationToThreshold> implements GenerateUniqueParameterization {

	/** The threshold-value */
	public abstract double threshold();
	
	/** The relation to the threshold to consider */
	public abstract RelationToValue relation();
}
