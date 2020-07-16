/* (C)2020 */
package org.anchoranalysis.anchor.plot;

import org.anchoranalysis.core.index.GetOperationFailedException;

/**
 * @author Owen Feehan
 * @param <T> container-item type
 * @param <S> return-type
 */
@FunctionalInterface
public interface GetForSeries<T, S> {
    S get(T item, int seriesNum) throws GetOperationFailedException;
}
