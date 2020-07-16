/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.optscheme.feedback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.mpp.sgmn.optscheme.step.Reporting;

@NoArgsConstructor
@AllArgsConstructor
public abstract class ReporterInterval<T> extends FeedbackReceiverBean<T> {

    // START BEAN FIELDS
    /**
     * How many iterations before printing a new report? Encoded in log10.
     *
     * <p>e.g. 0 implies every iteration, 1 implies every 10, 2 implies every 100 etc.
     */
    @BeanField @Getter @Setter private double aggIntervalLog10 = 0;
    // END BEAN FIELDS

    @Override
    public void reportItr(Reporting<T> reporting) {}

    protected int getAggInterval() {
        return (int) Math.pow(10.0, aggIntervalLog10);
    }
}
