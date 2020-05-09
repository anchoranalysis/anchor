package org.anchoranalysis.bean.shared.relation.threshold;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.bean.shared.relation.RelationBean;
import org.anchoranalysis.core.relation.RelationToValue;

public class RelationToConstant extends RelationToThreshold {

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
		long temp;
		temp = Double.doubleToLongBits(threshold);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RelationToConstant other = (RelationToConstant) obj;
		if (relation == null) {
			if (other.relation != null)
				return false;
		} else if (!relation.equals(other.relation))
			return false;
		if (Double.doubleToLongBits(threshold) != Double.doubleToLongBits(other.threshold))
			return false;
		return true;
	}

	@Override
	public String uniqueName() {
		return String.format(
			"%s_%s_%d",
			getClass().getCanonicalName(),
			relation.uniqueName(),
			threshold
		);
	}
}
