package org.anchoranalysis.bean.shared.relation.threshold;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.bean.shared.relation.RelationBean;
import org.anchoranalysis.core.relation.RelationToValue;

public class RelationToConstant extends RelationToThreshold {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// START BEAN PROPERTIES
	@BeanField @NonNegative
	private double threshold = -1;
	
	@BeanField
	private RelationBean relation;
	// END BEAN PROPERTIES
	
	public RelationToConstant() {
		// Bean constructor
	}
	
	public RelationToConstant(RelationBean relation, double threshold) {
		super();
		this.threshold = threshold;
		this.relation = relation;
	}

	@Override
	public double threshold() {
		return threshold;
	}

	@Override
	public RelationToValue relation() {
		return relation.create();
	}
	
	@Override
	public String toString() {
		return String.format("%s %f", relation, threshold);
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public RelationBean getRelation() {
		return relation;
	}

	public void setRelation(RelationBean relation) {
		this.relation = relation;
	}
}
