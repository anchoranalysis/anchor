package org.anchoranalysis.core.name;

import java.util.Optional;

public class MultiNameFactory {
	
	private MultiNameFactory() {}

	/**
	 * Creates either a multi-name that is either has a single part or a double part (with a group as first part)
	 * 
	 * @param groupIdentifier if present, a group identifier that becomes the first part
	 * @param nonGroupIdentifier the non-group part of the identifier that is present irrespective
	 * @return the created multi-name
	 */
	public static MultiName create(Optional<String> groupIdentifier, String nonGroupIdentifier) {
		return groupIdentifier.map( id->
			(MultiName) new CombinedName(id, nonGroupIdentifier)
		).orElseGet( ()->
			new SimpleName(nonGroupIdentifier)
		);
	}
}
