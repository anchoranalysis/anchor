/*-
 * #%L
 * anchor-mpp-feature
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

package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;

public class BBoxIntersection extends AddCriteriaPair {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private boolean suppressZ = false;
    // END BEAN PROPERTIES

    @Override
    public boolean includeMarks(
            VoxelizedMarkMemo mark1,
            VoxelizedMarkMemo mark2,
            ImageDimensions dimensions,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session,
            boolean do3D)
            throws IncludeMarksFailureException {

        BoundingBox box1 = mark1.getMark().boxAllRegions(dimensions);
        BoundingBox box2 = mark2.getMark().boxAllRegions(dimensions);

        if (suppressZ) {
            box1 = box1.flattenZ();
            box2 = box2.flattenZ();
        }

        return box1.intersection().existsWith(box2);
    }

    @Override
    public Optional<FeatureList<FeatureInputPairMemo>> orderedListOfFeatures() {
        return Optional.empty();
    }
}
