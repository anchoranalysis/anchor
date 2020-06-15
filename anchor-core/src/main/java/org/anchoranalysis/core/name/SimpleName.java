package org.anchoranalysis.core.name;

import java.util.Optional;

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
	public Optional<String> deriveAggregationKey() {
		return Optional.empty();
	}
	
	@Override
	public String nameWithoutAggregationKey() {
		return name;
	}
	
	@Override
	public int compareTo(MultiName other) {
		return name.compareTo(other.getPart(0));
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		SimpleName other = (SimpleName) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
