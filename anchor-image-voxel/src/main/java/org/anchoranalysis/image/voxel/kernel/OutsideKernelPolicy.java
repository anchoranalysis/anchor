/*-
 * #%L
 * anchor-image-voxel
 * %%
 * Copyright (C) 2010 - 2021 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.voxel.kernel;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;

/**
 * How to handle voxels whose neighbours are outside the scene.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public enum OutsideKernelPolicy {

    /**
     * Ignore any parts of the neighborhood that lie outside the scene. The second parameter here is
     * arbtirary.
     */
    IGNORE_OUTSIDE(true, false),

    /**
     * Pretend any parts of the neighborhood that lie outside the scene are being <i>on</i>-valued.
     */
    AS_ON(false, true),

    /**
     * Pretend any parts of the neighborhood that lie outside the scene are being <i>off</i>-valued.
     */
    AS_OFF(false, false);

    /**
     * When true, any parts of the neigborhood that lie outside the scene are not considered in
     * kernel neighborhoods.
     */
    private final boolean ignoreOutside;

    /**
     * If {@link #ignoreOutside} is false, then whether to treat voxels that lie outside the scene
     * as <i>on</i> (if true) or <i>off</i> (if false).
     */
    private final boolean outsideOn;

    /**
     * Multiplexes between {@link #AS_ON} and {@link #AS_OFF}.
     *
     * @param on if true, then {@link #AS_ON} is selected, otherwise {@link #AS_OFF}.
     * @return
     */
    public static OutsideKernelPolicy as(boolean on) {
        return on ? AS_ON : AS_OFF;
    }

    /**
     * Multiplexes between all three possible enums.
     *
     * @param ignoreOutside if true, then {@link #IGNORE_OUTSIDE} is selected.
     * @param outsideHigh if {@code ignoreOutside==false}, and determines whether {@link #AS_ON} (if
     *     true) is selected or {@link #AS_OFF} (if false).
     * @return
     */
    public static OutsideKernelPolicy of(boolean ignoreOutside, boolean outsideHigh) {
        if (ignoreOutside) {
            return IGNORE_OUTSIDE;
        } else {
            return as(outsideHigh);
        }
    }

    /**
     * When true, any parts of the neigborhood that lie outside the scene are not considered in
     * kernel neighborhoods.
     */
    public boolean isIgnoreOutside() {
        return ignoreOutside;
    }

    /**
     * If {@link #ignoreOutside} is false, then whether to treat voxels that lie outside the scene
     * as <i>on</i> (if true) or <i>off</i> (if false).
     */
    public boolean isOutsideOn() {
        return outsideOn;
    }

    /**
     * Multiplexes between three-values depending on which policy is currently selected.
     *
     * @param ignored the value to return if current policy is {@link #IGNORE_OUTSIDE}
     * @param asOn the value to return if current policy is {@link #AS_ON}
     * @param asOff the value to return if current policy is {@link #AS_OFF}
     * @return one of the three values above.
     */
    public int multiplex(int ignored, int asOn, int asOff) {
        switch (this) {
            case IGNORE_OUTSIDE:
                return ignored;
            case AS_ON:
                return asOn;
            case AS_OFF:
                return asOff;
            default:
                throw new AnchorImpossibleSituationException();
        }
    }
}
