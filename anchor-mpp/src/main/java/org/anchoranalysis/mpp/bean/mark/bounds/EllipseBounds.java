/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.mpp.bean.mark.bounds;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Optional;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.orientation.Orientation;
import org.anchoranalysis.image.core.orientation.Orientation2D;
import org.anchoranalysis.mpp.bean.bound.Bound;
import org.anchoranalysis.mpp.bean.bound.BoundUnitless;

@NoArgsConstructor
public class EllipseBounds extends EllipseBoundsWithoutRotation {

    /** */
    private static final long serialVersionUID = 5833714580114414447L;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private Bound rotationAngle;
    // END BEAN PROPERTIES

    // Constructor
    public EllipseBounds(Bound radiusBnd) {
        super(radiusBnd);
        rotationAngle = new BoundUnitless(0, 2 * Math.PI);
    }

    // Copy Constructor
    public EllipseBounds(EllipseBounds src) {
        super(src);
        rotationAngle = src.rotationAngle.duplicate();
    }

    @Override
    public String describeBean() {
        return String.format(
                "%s, radius=(%s), rotation=(%s)",
                getBeanName(), getRadius().toString(), rotationAngle.toString());
    }

    @Override
    public Orientation randomOrientation(
            RandomNumberGenerator randomNumberGenerator, Optional<Resolution> resolution) {
        return new Orientation2D(
                getRotationAngle().resolve(resolution, false).randOpen(randomNumberGenerator));
    }
}
