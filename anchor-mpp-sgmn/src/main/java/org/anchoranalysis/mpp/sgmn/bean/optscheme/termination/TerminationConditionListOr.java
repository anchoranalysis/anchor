/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.optscheme.termination;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.anchoranalysis.bean.annotation.BeanField;
import org.anchoranalysis.core.log.MessageLogger;

// An OR list of termination conditions
@NoArgsConstructor
public class TerminationConditionListOr extends TerminationCondition {

    // START BEAN PROPERTIES
    @BeanField @Getter @Setter private List<TerminationCondition> list = new ArrayList<>();
    // END BEAN PROPERTIES

    public TerminationConditionListOr(TerminationCondition cond1, TerminationCondition cond2) {
        this();
        list.add(cond1);
        list.add(cond2);
    }

    public boolean add(TerminationCondition tc) {
        return list.add(tc);
    }

    public int size() {
        return list.size();
    }

    @Override
    public boolean continueIterations(int crntIter, double score, int size, MessageLogger logger) {

        for (TerminationCondition tc : this.list) {
            if (!tc.continueIterations(crntIter, score, size, logger)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void init() {

        for (TerminationCondition tc : this.list) {
            tc.init();
        }
    }
}
