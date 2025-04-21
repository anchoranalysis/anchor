/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.bean.unitvalue.distance;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.AllowEmpty;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.image.core.dimensions.SpatialUnits;
import org.anchoranalysis.image.core.dimensions.UnitConverter;
import org.anchoranalysis.spatial.orientation.DirectionVector;

/**
 * Implementation of {@link UnitValueDistance} that specifies a value in physical units, as
 * described by particular recognized strings.
 *
 * <p>See {@link SpatialUnits} for the recognized strings.
 *
 * @author Owen Feehan
 */
public class DistancePhysical extends UnitValueDistance {

    /** */
    private static final long serialVersionUID = 1L;

    // START BEAN PROPERTIES
    /** The value in units of type {@code unitType}. */
    @BeanField @Getter @Setter private double value;

    /** A string indicating type of units to use, as per {@link SpatialUnits}. */
    @BeanField @AllowEmpty @Getter @Setter private String unitType;

    // END BEAN PROPERTIES

    @Override
    public double resolve(Optional<UnitConverter> unitConverter, DirectionVector direction)
            throws OperationFailedException {

        if (unitConverter.isPresent()) {

            double valueAsBase = SpatialUnits.convertFromUnits(value, unitType);

            return unitConverter.get().fromPhysicalDistance(valueAsBase, direction);
        } else {
            throw new OperationFailedException(
                    "Image-resolution is missing, so cannot calculate physical distances");
        }
    }
}
