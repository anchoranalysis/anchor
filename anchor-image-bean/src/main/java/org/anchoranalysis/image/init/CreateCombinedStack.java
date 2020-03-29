package org.anchoranalysis.image.init;

import org.anchoranalysis.core.name.provider.INamedProvider;
import org.anchoranalysis.image.stack.Stack;

public class CreateCombinedStack {
	
	public static INamedProvider<Stack> apply( ImageInitParams so ) {
		return new CombineDiverseProvidersAsStacks(
			so.getStackCollection(),
			so.getChnlCollection(),
			so.getBinaryImageCollection()
		);
	}
}
