/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.mark.factory;

import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.points.MarkPointList;

public class MarkPointListFactory extends MarkFactory {

    @Override
    public Mark create() {
        return new MarkPointList();
    }
}
