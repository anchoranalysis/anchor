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
package org.anchoranalysis.image.bean.size;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.NonNegative;
import org.anchoranalysis.core.geometry.Point3i;

/**
 * Padding (whitespace of certain extent) placed around an object in XY direction and in Z
 * direction.
 *
 * <p>It is valid-state for padding to be 0 in all dimensions (in which case no padding is applied).
 *
 * @author Owen Feehan
 */
public class Padding extends AnchorBean<Padding> {

    // START BEAN PROPERTIES
    /**
     * Padding placed on each side of the outputted image (if it's within the image) in XY
     * directions
     */
    @BeanField @Getter @Setter @NonNegative private int paddingXY = 0;

    /**
     * Padding placed on each side of the outputted image (if it's within the image) in Z direction
     */
    @BeanField @Getter @Setter @NonNegative private int paddingZ = 0;
    // END BEAN PROPERTIES

    public Point3i asPoint() {
        return new Point3i(paddingXY, paddingXY, paddingZ);
    }

    public boolean noPadding() {
        return paddingXY == 0 && paddingZ == 0;
    }
}
