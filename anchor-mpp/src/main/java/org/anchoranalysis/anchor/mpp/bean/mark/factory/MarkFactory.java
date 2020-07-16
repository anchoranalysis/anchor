/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.mark.factory;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.bean.AnchorBean;

public abstract class MarkFactory extends AnchorBean<MarkFactory> {

    public abstract Mark create();
}
