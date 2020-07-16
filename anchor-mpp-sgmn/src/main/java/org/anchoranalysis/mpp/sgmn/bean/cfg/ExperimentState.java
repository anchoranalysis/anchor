/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.bean.cfg;

import org.anchoranalysis.io.output.bound.BoundOutputManagerRouteErrors;

// State that only needs to be initialized once can be shared across many calls to the algoritm
public interface ExperimentState {

    void outputBeforeAnyTasksAreExecuted(BoundOutputManagerRouteErrors outputManager);

    // We just need any single kernel proposer to write out
    void outputAfterAllTasksAreExecuted(BoundOutputManagerRouteErrors outputManager);
}
