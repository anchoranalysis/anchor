/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.mark.factory;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.mark.conic.MarkEllipsoid;
import org.anchoranalysis.bean.annotation.BeanField;

public class MarkEllipsoidFactory extends MarkFactory {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private double shellRad = 0.1;
    // END BEAN PROPERTIES

    @Override
    public Mark create() {
        MarkEllipsoid mark = new MarkEllipsoid();
        mark.setShellRad(shellRad);
        return mark;
    }
}
