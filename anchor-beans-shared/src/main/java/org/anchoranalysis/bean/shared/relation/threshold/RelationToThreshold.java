package org.anchoranalysis.bean.shared.relation.threshold;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.relation.RelationToValue;

/**
 * A threshold and a relation to it, allowing for tests of a value in relation to a threshold.
 * 
 * @author Owen Feehan
 *
 */
public abstract class RelationToThreshold extends AnchorBean<RelationToThreshold> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** The threshold-value */
	public abstract double threshold();
	
	/** The relation to the threshold to consider */
	public abstract RelationToValue relation();
	
}
