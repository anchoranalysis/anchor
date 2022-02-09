/*-
 * #%L
 * anchor-image-bean
 * %%
 * Copyright (C) 2010 - 2022 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
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
package org.anchoranalysis.image.bean.nonbean.spatial.arrange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Describes a {@link BoundingBox} that is optionally enclosed by a larger containing {@link
 * BoundingBox} to given padding.
 *
 * <p>No checks currently occur that {@code enclosing} fully contains {@code box}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class BoundingBoxEnclosed {

    /** The {@link BoundingBox} in which the image should be placed. This excludes padding. */
    @Getter private BoundingBox box;

    /**
     * An enclosing box (equal to or larger than {@code box} covering all screen-space used for this
     * entity, including padding.
     *
     * <p>This should be identical to {@code box} if there is no padding.
     */
    @Getter private BoundingBox enclosing;

    /**
     * Create with a box that has no padding.
     *
     * <p>i.e. this box is enclosed by itself.
     *
     * @param box the box.
     */
    public BoundingBoxEnclosed(BoundingBox box) {
        this.box = box;
        this.enclosing = box;
    }
}
