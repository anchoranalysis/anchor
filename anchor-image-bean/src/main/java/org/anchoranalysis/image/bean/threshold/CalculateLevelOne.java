package org.anchoranalysis.image.bean.threshold;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.histogram.Histogram;

public abstract class CalculateLevelOne extends CalculateLevel {

	// START BEAN PROPERTIES
	@BeanField
	private CalculateLevel calculateLevel;
	// END BEAN PROPERTIES
	
	protected int calculateLevelIncoming(Histogram hist) throws OperationFailedException {
		return calculateLevel.calculateLevel(hist);
	}
	
	public CalculateLevel getCalculateLevel() {
		return calculateLevel;
	}

	public void setCalculateLevel(CalculateLevel calculateLevel) {
		this.calculateLevel = calculateLevel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((calculateLevel == null) ? 0 : calculateLevel.hashCode());
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
		CalculateLevelOne other = (CalculateLevelOne) obj;
		if (calculateLevel == null) {
			if (other.calculateLevel != null)
				return false;
		} else if (!calculateLevel.equals(other.calculateLevel))
			return false;
		return true;
	}
}
