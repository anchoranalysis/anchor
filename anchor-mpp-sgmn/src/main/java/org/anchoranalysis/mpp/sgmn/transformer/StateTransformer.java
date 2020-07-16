/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.transformer;

import org.anchoranalysis.core.error.OperationFailedException;

@FunctionalInterface
public interface StateTransformer<W, X> {

    X transform(W item, TransformationContext context) throws OperationFailedException;
}
