/* (C)2020 */
package org.anchoranalysis.image.bean.orientation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.orientation.DirectionVector;

@NoArgsConstructor
@AllArgsConstructor
public class DirectionVectorBean extends AnchorBean<DirectionVectorBean> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private double x = 0;

    @BeanField @Getter @Setter private double y = 0;

    @BeanField @Getter @Setter private double z = 0;
    // END BEAN PROPERTIES

    public DirectionVectorBean(DirectionVector vector) {
        this.x = vector.getX();
        this.y = vector.getY();
        this.z = vector.getZ();
    }

    public DirectionVector createVector() {
        return new DirectionVector(x, y, z);
    }
}
