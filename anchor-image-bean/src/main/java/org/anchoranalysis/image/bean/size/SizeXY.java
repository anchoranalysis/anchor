/* (C)2020 */
package org.anchoranalysis.image.bean.size;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.extent.Extent;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SizeXY extends AnchorBean<SizeXY> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private int width;

    @BeanField @Getter @Setter private int height;
    // END BEAN PROPERTIES

    /**
     * Constructor
     *
     * <p>Note the z-dimension in extent is ignored.
     *
     * @param extent extent
     */
    public SizeXY(Extent extent) {
        this(extent.getX(), extent.getY());
    }

    public Extent asExtent() {
        return new Extent(width, height, 1);
    }
}
