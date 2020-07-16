/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.transformer;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;

/**
 * Combines a transformation from S->U with U->T
 *
 * @author Owen Feehan
 * @param <S> source type
 * @param <T> destination type
 * @param <U> intermediate type
 */
public class Compose<S, T, U> extends StateTransformerBean<S, T> {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private StateTransformerBean<S, U> first;

    @BeanField @Getter @Setter private StateTransformerBean<U, T> second;
    // END BEAN PROPERTIES

    @Override
    public T transform(S in, TransformationContext context) throws OperationFailedException {

        U inter = first.transform(in, context);

        return second.transform(inter, context);
    }
}
