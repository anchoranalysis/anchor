/* (C)2020 */
package org.anchoranalysis.io.bean.object.writer;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.ObjectDrawAttributes;
import org.anchoranalysis.anchor.overlay.writer.PrecalcOverlay;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;
import org.anchoranalysis.image.stack.rgb.RGBStack;

@NoArgsConstructor
@AllArgsConstructor
public class Combine extends DrawObject {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<DrawObject> list;
    // END BEAN PROPERTIES

    @Override
    public PrecalcOverlay precalculate(ObjectWithProperties mask, ImageDimensions dim)
            throws CreateException {

        List<PrecalcOverlay> listPrecalc = new ArrayList<>();

        for (DrawObject writer : list) {
            listPrecalc.add(writer.precalculate(mask, dim));
        }

        return new PrecalcOverlay(mask) {

            @Override
            public void writePrecalculatedMask(
                    RGBStack background,
                    ObjectDrawAttributes attributes,
                    int iteration,
                    BoundingBox restrictTo)
                    throws OperationFailedException {

                for (PrecalcOverlay preCalc : listPrecalc) {
                    preCalc.writePrecalculatedMask(background, attributes, iteration, restrictTo);
                }
            }
        };
    }
}
