/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bean.proposer;

import org.anchoranalysis.bean.NullParamsBean;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.extent.ImageResolution;

public abstract class ScalarProposer extends NullParamsBean<ScalarProposer> {

    public abstract double propose(RandomNumberGenerator randomNumberGenerator, ImageResolution res)
            throws OperationFailedException;
}
