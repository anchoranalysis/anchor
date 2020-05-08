package org.anchoranalysis.image.bean.threshold.relation;

import org.anchoranalysis.bean.shared.relation.threshold.RelationToThreshold;

public abstract class BinaryVoxelsBase extends RelationToThreshold {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public int hashCode() {
		return 51;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

	// This is sufficient for all base-classes, as we can rely on them not being further parameterized
	@Override
	public String uniqueName() {
		return getClass().getCanonicalName();
	}
}
