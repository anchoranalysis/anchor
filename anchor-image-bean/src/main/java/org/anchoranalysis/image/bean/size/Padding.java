package org.anchoranalysis.image.bean.size;

import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.core.geometry.Point3i;
import lombok.Getter;
import lombok.Setter;

/**
 * Padding (whitespace of certain extent) placed around an object in XY direction and in Z direction.
 * <p>
 * It is valid-state for padding to be 0 in all dimensions (in which case no padding is applied).
 * 
 * @author Owen Feehan
 *
 */
public class Padding extends AnchorBean<Padding> {

    // START BEAN PROPERTIES
    /**
     * Padding placed on each side of the outputted image (if it's within the image) in XY
     * directions
     */
    @BeanField @Getter @Setter @NonNegative private int paddingXY = 0;

    /**
     * Padding placed on each side of the outputted image (if it's within the image) in Z direction
     */
    @BeanField @Getter @Setter @NonNegative private int paddingZ = 0;
    // END BEAN PROPERTIES
    
    public Point3i asPoint() {
        return new Point3i(paddingXY, paddingXY, paddingZ);
    }
    
    public boolean noPadding() {
        return paddingXY==0 && paddingZ==0;
    }
}
