/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.bean.list.FeatureList;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.extent.ImageDimensions;

public class AddCriteriaOr extends AddCriteriaPair {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<AddCriteriaPair> list = new ArrayList<>();
    // END BEAN PROPERTIES

    @Override
    public boolean includeMarks(
            VoxelizedMarkMemo mark1,
            VoxelizedMarkMemo mark2,
            ImageDimensions dimensions,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session,
            boolean do3D)
            throws IncludeMarksFailureException {

        for (int i = 0; i < list.size(); i++) {

            AddCriteriaPair acp = list.get(i);

            if (acp.includeMarks(mark1, mark2, dimensions, session, do3D)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Optional<FeatureList<FeatureInputPairMemo>> orderedListOfFeatures()
            throws CreateException {
        return OrderedFeatureListCombine.combine(list);
    }
}
