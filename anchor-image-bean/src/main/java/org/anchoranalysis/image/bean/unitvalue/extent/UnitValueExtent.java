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

package org.anchoranalysis.image.bean.unitvalue.extent;

import java.util.Optional;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.image.bean.nonbean.UnitValueException;
import org.anchoranalysis.image.core.dimensions.UnitConverter;

/**
 * A base class for a value that describes an <b>area or volume</b> measurement, which can then be resolved to a number of voxels.
 * 
 * @author Owen
 *
 */
public abstract class UnitValueExtent extends AnchorBean<UnitValueExtent> {

    /**
     * Resolves a measurement of area/volume (in whatever units) to units corresponding to the image
     * pixels/voxels.
     *
     * @param unitConverter converts from voxelized units to different physical measurements of area / volume / distance.
     * @return the resolved-value (pixels for area, voxels for volume).
     * @throws UnitValueException if missing a {@link UnitConverter}, when needed for resolution.
     */
    public abstract double resolveToVoxels(Optional<UnitConverter> unitConverter)
            throws UnitValueException;
}
