/* (C)2020 */
package org.anchoranalysis.image.bean.threshold;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.histogram.Histogram;

@EqualsAndHashCode(callSuper = false)
public abstract class CalculateLevelOne extends CalculateLevel {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private CalculateLevel calculateLevel;
    // END BEAN PROPERTIES

    protected int calculateLevelIncoming(Histogram hist) throws OperationFailedException {
        return calculateLevel.calculateLevel(hist);
    }
}
