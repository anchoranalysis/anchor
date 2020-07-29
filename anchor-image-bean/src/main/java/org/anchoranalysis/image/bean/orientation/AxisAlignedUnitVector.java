package org.anchoranalysis.image.bean.orientation;

import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.axis.AxisTypeConverter;
import org.anchoranalysis.core.axis.AxisTypeException;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.orientation.DirectionVector;
import lombok.Getter;
import lombok.Setter;

public class AxisAlignedUnitVector extends DirectionVectorBean {

    // START BEAN PROPERTIES
    /** Which axis the unit-vector will align with. {@code x} or {@code y} or {@code z} */
    @BeanField @Getter @Setter
    private String axis;
    // END BEAN PROPRERTIES
    
    @Override
    public DirectionVector createVector() throws CreateException {
        try {
            return new DirectionVector(
               AxisTypeConverter.createFromString(axis)
            );
        } catch (AxisTypeException e) {
            throw new CreateException(e);
        }
    }

}
