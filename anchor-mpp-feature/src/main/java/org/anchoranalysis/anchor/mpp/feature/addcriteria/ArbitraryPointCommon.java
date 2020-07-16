/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.mark.points.MarkPointList;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;

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
            ImageDimensions dimensions,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session,
            boolean do3D)
            throws IncludeMarksFailureException {

        BoundingBox bbox1 = mark1.getMark().bboxAllRegions(dimensions);
        BoundingBox bbox2 = mark2.getMark().bboxAllRegions(dimensions);

        // If their bounding boxes don't intersect, they cannot possibly have an overlapping point
        if (!bbox1.intersection().existsWith(bbox2)) {
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
