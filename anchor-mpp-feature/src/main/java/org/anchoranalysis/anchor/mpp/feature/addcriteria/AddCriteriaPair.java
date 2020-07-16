/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.addcriteria;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.feature.input.memo.FeatureInputPairMemo;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.mpp.pair.Pair;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;
import org.anchoranalysis.bean.AnchorBean;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.feature.nrg.NRGStackWithParams;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorMulti;
import org.anchoranalysis.image.extent.ImageDimensions;

public abstract class AddCriteriaPair extends AnchorBean<AddCriteriaPair>
        implements AddCriteria<Pair<Mark>> {

    @Override
    public Optional<Pair<Mark>> generateEdge(
            VoxelizedMarkMemo mark1,
            VoxelizedMarkMemo mark2,
            NRGStackWithParams nrgStack,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session,
            boolean do3D)
            throws CreateException {

        try {
            if (includeMarks(mark1, mark2, nrgStack.getDimensions(), session, do3D)) {
                return Optional.of(new Pair<Mark>(mark1.getMark(), mark2.getMark()));
            }
        } catch (IncludeMarksFailureException e) {
            throw new CreateException(e);
        }

        return Optional.empty();
    }

    public abstract boolean includeMarks(
            VoxelizedMarkMemo mark1,
            VoxelizedMarkMemo mark2,
            ImageDimensions dimensions,
            Optional<FeatureCalculatorMulti<FeatureInputPairMemo>> session,
            boolean do3D)
            throws IncludeMarksFailureException;

    @Override
    public String getBeanDscr() {
        return getBeanName();
    }
}
