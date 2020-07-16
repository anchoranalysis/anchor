/* (C)2020 */
package org.anchoranalysis.mpp.io.bean.report.feature;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.bean.provider.MarkProvider;
import org.anchoranalysis.anchor.mpp.feature.bean.mark.FeatureInputMark;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.extent.ImageDimensions;

public class ReportFeatureOnMark extends ReportFeatureForMPP<FeatureInputMark> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private MarkProvider markProvider;
    // END BEAN PROPERTIES

    @Override
    public boolean isNumeric() {
        return true;
    }

    @Override
    public String genFeatureStringFor(MPPInitParams so, Logger logger)
            throws OperationFailedException {

        // Maybe we should duplicate the providers?
        try {
            init(so, logger);
            markProvider.initRecursive(so, logger);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        Optional<Mark> mark;
        try {
            mark = markProvider.create();
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }

        if (!mark.isPresent()) {
            return "no mark returned";
        }

        try {
            FeatureCalculatorSingle<FeatureInputMark> session = createAndStartSession();

            ImageDimensions dimensions = createImageDim();

            double val = session.calc(new FeatureInputMark(mark.get(), Optional.of(dimensions)));
            return Double.toString(val);
        } catch (FeatureCalcException | CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
