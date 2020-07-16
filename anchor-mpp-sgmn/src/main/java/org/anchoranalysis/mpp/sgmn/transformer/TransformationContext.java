/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.transformer;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.anchor.mpp.proposer.error.ErrorNode;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.mpp.sgmn.kernel.KernelCalcContext;

@AllArgsConstructor
@Value
public class TransformationContext {

    private final ImageDimensions dimensions;
    private final KernelCalcContext kernelCalcContext;
    private final Logger logger;

    public TransformationContext replaceError(ErrorNode errorNode) {
        return new TransformationContext(
                dimensions, kernelCalcContext.replaceError(errorNode), logger);
    }
}
