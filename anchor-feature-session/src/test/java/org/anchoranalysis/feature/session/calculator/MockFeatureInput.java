package org.anchoranalysis.feature.session.calculator;

import org.anchoranalysis.feature.input.FeatureInput;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/** An input with well defined equals() and hash-code() */
class MockFeatureInput extends FeatureInput {
	
	private String id;
	
	public MockFeatureInput(String id) {
		super();
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (obj == this) { return true; }
		if (obj.getClass() != getClass()) {
			return false;
		}
		MockFeatureInput rhs = (MockFeatureInput) obj;
		return new EqualsBuilder()
             .append(id, rhs.id)
             .isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.toHashCode();
	}
}