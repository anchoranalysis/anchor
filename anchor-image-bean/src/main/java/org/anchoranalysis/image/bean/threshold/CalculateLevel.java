/* (C)2020 */
package org.anchoranalysis.image.bean.threshold;

import org.anchoranalysis.bean.NullParamsBean;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.histogram.Histogram;

/**
 * Calculates a level (threshold) from a histogram
 *
 * <p>A well-behaved CalculateLevel should implements equals() and hashCode() If it doesn't, these
 * methods should assert(false)
 *
 * @author Owen Feehan
 */
public abstract class CalculateLevel extends NullParamsBean<CalculateLevel> {

    public abstract int calculateLevel(Histogram h) throws OperationFailedException;

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();
}
