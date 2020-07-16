/* (C)2020 */
package org.anchoranalysis.mpp.io.bean.report.feature;

import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.anchor.mpp.bean.cfg.CfgProvider;
import org.anchoranalysis.anchor.mpp.bean.init.MPPInitParams;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.bean.cfg.FeatureInputCfg;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.InitException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.feature.calc.FeatureCalcException;
import org.anchoranalysis.feature.session.calculator.FeatureCalculatorSingle;
import org.anchoranalysis.image.extent.ImageDimensions;

public class ReportFeatureOnCfg extends ReportFeatureForMPP<FeatureInputCfg> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private CfgProvider cfgProvider;
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
            cfgProvider.initRecursive(so, logger);
        } catch (InitException e) {
            throw new OperationFailedException(e);
        }

        try {
            Cfg cfg = cfgProvider.create();

            ImageDimensions dimensions = createImageDim();

            FeatureCalculatorSingle<FeatureInputCfg> session = createAndStartSession();

            double val = session.calc(new FeatureInputCfg(cfg, Optional.of(dimensions)));
            return Double.toString(val);

        } catch (FeatureCalcException | CreateException e) {
            throw new OperationFailedException(e);
        }
    }
}
