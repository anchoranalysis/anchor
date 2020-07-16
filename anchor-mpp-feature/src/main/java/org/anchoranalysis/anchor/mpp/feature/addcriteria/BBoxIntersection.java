/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
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

        BoundingBox bbox1 = mark1.getMark().bboxAllRegions(dimensions);
        BoundingBox bbox2 = mark2.getMark().bboxAllRegions(dimensions);

        if (suppressZ) {
            bbox1 = bbox1.flattenZ();
            bbox2 = bbox2.flattenZ();
        }

        return bbox1.intersection().existsWith(bbox2);
    }

    @Override
    public Optional<FeatureList<FeatureInputPairMemo>> orderedListOfFeatures() {
        return Optional.empty();
    }
}
