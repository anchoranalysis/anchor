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
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.core.dimensions.UnitConverter;
import org.anchoranalysis.spatial.orientation.DirectionVector;

/**
 * Implementation of {@link UnitValueDistance} that specifies a value in voxels, ignoring any
 * physical image resolution.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor
public class DistanceVoxels extends UnitValueDistance {

    /** */
    private static final long serialVersionUID = 1L;

    // START BEAN PROPERTIES
    /** The distance in units of voxels. */
    @BeanField @Getter @Setter private double value;

    // END BEAN PROPERTIES

    /**
     * Create with a particular value.
     *
     * @param value the distance in units of voxels.
     */
    public DistanceVoxels(double value) {
        this.value = value;
    }

    @Override
    public double resolve(Optional<UnitConverter> unitConverter, DirectionVector direction) {
        return value;
    }
}
