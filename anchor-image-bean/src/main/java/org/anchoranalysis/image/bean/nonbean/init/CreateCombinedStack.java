/* (C)2020 */
package org.anchoranalysis.image.bean.nonbean.init;

import org.anchoranalysis.core.name.provider.NamedProvider;
import org.anchoranalysis.image.stack.Stack;

public class CreateCombinedStack {

    private CreateCombinedStack() {}

    public static NamedProvider<Stack> apply(ImageInitParams so) {
        return new CombineDiverseProvidersAsStacks(
                so.getStackCollection(), so.getChnlCollection(), so.getBinaryImageCollection());
    }
}
