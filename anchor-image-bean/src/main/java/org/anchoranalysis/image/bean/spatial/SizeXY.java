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

package org.anchoranalysis.image.bean.spatial;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.bean.annotation.Positive;
import org.anchoranalysis.spatial.box.Extent;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SizeXY extends AnchorBean<SizeXY> {

    // START BEAN PROPERTIES
    /** Size in X dimension. */
    @BeanField @Positive @Getter @Setter private int width;

    /** Size in Y dimension. */
    @BeanField @Positive @Getter @Setter private int height;
    // END BEAN PROPERTIES

    /**
     * Constructor
     *
     * <p>Note the z-dimension in extent is ignored.
     *
     * @param extent extent
     */
    public SizeXY(Extent extent) {
        this(extent.x(), extent.y());
    }

    /**
     * Creates an extent with identical width and height and depth (z-extent) of 1
     *
     * @return the newly created extent
     */
    public Extent asExtent() {
        return asExtent(1);
    }

    /**
     * Creates an extent with identical width and height and a specific depth (z-extent)
     *
     * @param depth the depth for the extent
     * @return the newly created extent
     */
    public Extent asExtent(int depth) {
        return new Extent(width, height, depth);
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", width, height);
    }
}
