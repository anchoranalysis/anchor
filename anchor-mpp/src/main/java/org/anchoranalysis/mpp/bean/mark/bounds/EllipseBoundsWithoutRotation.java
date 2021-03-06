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

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.mpp.bean.bound.Bound;
import org.anchoranalysis.mpp.bean.bound.OrientableBounds;

@NoArgsConstructor
@AllArgsConstructor
public abstract class EllipseBoundsWithoutRotation extends OrientableBounds {

    /** */
    private static final long serialVersionUID = -1109615535786453388L;

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private Bound radius;
    // END BEAN PROPERTIES

    // Copy Constructor
    protected EllipseBoundsWithoutRotation(EllipseBoundsWithoutRotation src) {
        radius = src.radius.duplicate();
    }

    // NB objects are scaled in pre-rotated position i.e. when aligned to axes
    public void scaleXY(double multFactor) {
        this.radius.scale(multFactor);
    }

    @Override
    public double getMinResolved(Optional<Resolution> resolution, boolean do3D) {
        return radius.getMinResolved(resolution, do3D);
    }

    @Override
    public double getMaxResolved(Optional<Resolution> resolution, boolean do3D) {
        return radius.getMaxResolved(resolution, do3D);
    }
}
