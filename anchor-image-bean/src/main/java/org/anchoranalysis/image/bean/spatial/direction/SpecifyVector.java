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

package org.anchoranalysis.image.bean.spatial.direction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.spatial.orientation.DirectionVector;

/**
 * Specifies each axis-component of a vector in a particular direction.
 *
 * @author Owen
 */
@NoArgsConstructor
@AllArgsConstructor
public class SpecifyVector extends DirectionVectorBean {

    // START BEAN PROPERTIES
    /** Component of the vector along the <b>x-axis</b>. */
    @BeanField @Getter @Setter private double x = 0;

    /** Component of the vector along the <b>y-axis</b>. */
    @BeanField @Getter @Setter private double y = 0;

    /** Component of the vector along the <b>z-axis</b>. */
    @BeanField @Getter @Setter private double z = 0;
    // END BEAN PROPERTIES

    /**
     * Create from a {@link DirectionVector}.
     *
     * @param vector the vector from which components are copied.
     */
    public SpecifyVector(DirectionVector vector) {
        this.x = vector.x();
        this.y = vector.y();
        this.z = vector.z();
    }

    @Override
    public DirectionVector createVector() {
        return new DirectionVector(x, y, z);
    }
}
