/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.optscheme;

import org.anchoranalysis.bean.AnchorBean;

public abstract class ExtractScoreSize<T> extends AnchorBean<ExtractScoreSize<T>> {

    public abstract double extractScore(T item);

    public abstract int extractSize(T item);
}
