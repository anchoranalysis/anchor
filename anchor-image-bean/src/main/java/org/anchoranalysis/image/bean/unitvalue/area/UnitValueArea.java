/* (C)2020 */
package org.anchoranalysis.image.bean.unitvalue.area;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.bean.unitvalue.areavolume.UnitValueAreaOrVolume;

@NoArgsConstructor
public abstract class UnitValueArea extends UnitValueAreaOrVolume {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private double value;
    // END BEAN PROPERTIES

    public UnitValueArea(double value) {
        this.value = value;
    }
}
