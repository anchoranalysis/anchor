/* (C)2020 */
package org.anchoranalysis.anchor.mpp.mark;

import org.anchoranalysis.bean.AnchorBean;

public abstract class MarkDistance extends AnchorBean<MarkDistance> implements CompatibleWithMark {

    public abstract double distance(Mark mark1, Mark mark2) throws UnsupportedMarkTypeException;
}
