/* (C)2020 */
package org.anchoranalysis.anchor.mpp.proposer.visualization;

import org.anchoranalysis.anchor.mpp.cfg.ColoredCfg;

@FunctionalInterface
public interface CreateProposalVisualization {

    void addToCfg(ColoredCfg cfg);
}
