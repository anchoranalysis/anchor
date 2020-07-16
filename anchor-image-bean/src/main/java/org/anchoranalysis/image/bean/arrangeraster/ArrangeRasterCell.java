/* (C)2020 */
package org.anchoranalysis.image.bean.arrangeraster;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.image.bean.nonbean.arrangeraster.ArrangeRaster;

public class ArrangeRasterCell extends AnchorBean<ArrangeRasterCell> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private ArrangeRaster arrangeRaster;

    @BeanField @NonNegative @Getter @Setter private int row;

    @BeanField @NonNegative @Getter @Setter private int col;
    // END BEAN PROPERTIES
}
