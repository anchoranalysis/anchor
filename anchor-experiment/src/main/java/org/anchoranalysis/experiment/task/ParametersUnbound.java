/* (C)2020 */
package org.anchoranalysis.experiment.task;

import lombok.Value;

/**
 * @author Owen Feehan
 * @param <T> input-object type
 * @param <S> shared-object type
 */
@Value
public class ParametersUnbound<T, S> {

    ParametersExperiment parametersExperiment;

    T inputObject;

    S sharedState;

    boolean suppressExceptions;
}
