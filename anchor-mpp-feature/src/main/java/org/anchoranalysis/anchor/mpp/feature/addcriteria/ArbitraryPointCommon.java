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
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.mark.points.MarkPointList;
import org.anchoranalysis.anchor.mpp.mark.voxelized.memo.VoxelizedMarkMemo;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Dimensions;

/**
 * If one arbitrarily point overlaps between two MarkPointList then TRUE, otherwise FALSE
 *
 * <p>This is useful for mutually exclusive sets of points, where iff one arbitrary point intersects
 * with another set, then they must be identical.
 */
public class ArbitraryPointCommon extends AddCriteriaPair {

    @Override
    public boolean includeMarks(
            VoxelizedMarkMemo mark1,
            VoxelizedMarkMemo mark2,
            Dimensions dimensions,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session,
            boolean do3D)
            throws IncludeMarksFailureException {

        BoundingBox box1 = mark1.getMark().boxAllRegions(dimensions);
        BoundingBox box2 = mark2.getMark().boxAllRegions(dimensions);

        // If their bounding boxes don't intersect, they cannot possibly have an overlapping point
        if (!box1.intersection().existsWith(box2)) {
            return false;
        }

        MarkPointList mark1Cast = (MarkPointList) mark1.getMark();
        MarkPointList mark2Cast = (MarkPointList) mark2.getMark();

        // Check for intersection of an arbitrary point
        return mark2Cast.getPoints().contains(mark1Cast.getPoints().get(0));
    }

    @Override
    public Optional<FeatureList<FeatureInputPairMemo>> orderedListOfFeatures() {
        return Optional.empty();
    }
}
