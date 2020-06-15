package org.anchoranalysis.core.name;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyRuntimeException;

/**
 * A name with only one part, and is always unique
 * 
 * @author Owen Feehan
 *
 */
public class SimpleName implements MultiName {

	private String name;
	
	public SimpleName(String name) {
		super();
		this.name = name;
	}
	
	@Override
	public int numParts() {
		return 1;
	}

	@Override
	public String getPart(int index) {
		if (index!=0) {
			throw new AnchorFriendlyRuntimeException("An index other than 0 was passed");
		}
		return name;
	}

	@Override
	public String getAggregateKeyName() {
		// There is no higher level of aggregation
		return name;
	}
	
	@Override
	public int compareTo(MultiName other) {
		return name.compareTo(other.getPart(0));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleName other = (SimpleName) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
