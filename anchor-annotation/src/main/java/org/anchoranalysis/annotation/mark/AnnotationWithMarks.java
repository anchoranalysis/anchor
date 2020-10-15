/*-
 * #%L
 * anchor-annotation
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

package org.anchoranalysis.annotation.mark;

import org.anchoranalysis.annotation.Annotation;
import org.anchoranalysis.image.dimensions.Dimensions;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.mark.MarkCollection;

/**
 * An image-annotation that involves a mark-collection.
 *
 * @author Owen Feehan
 */
public interface AnnotationWithMarks extends Annotation {

    /**
     * The marks associated with the annotation
     *
     * @return the marks
     */
    MarkCollection marks();

    /**
     * The region(s) of the marks that specifies the annotation.
     *
     * <p>As marks have multiple regions, this identifies which regions are included in the
     * annotation.
     *
     * @return the identifier
     */
    RegionMembershipWithFlags region();

    /**
     * Creates an object-collection that is a voxelized representation of the marks in the
     * annotation.
     *
     * @param dimensions size of image the annotations pertain to.
     * @return newly created objects
     */
    default ObjectCollection convertToObjects(Dimensions dimensions) {
        return marks().deriveObjects(dimensions, region()).withoutProperties();
    }
}
