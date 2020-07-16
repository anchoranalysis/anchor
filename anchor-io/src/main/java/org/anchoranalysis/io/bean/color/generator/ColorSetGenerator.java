/* (C)2020 */
package org.anchoranalysis.io.bean.color.generator;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.color.ColorList;
import org.anchoranalysis.core.error.OperationFailedException;

// The IInitProposerSharedObjects is optional, it's not guaranteed to be called in all situations
// that genColors is used
public abstract class ColorSetGenerator extends AnchorBean<ColorSetGenerator> {

    public abstract ColorList generateColors(int numberColors) throws OperationFailedException;
}
