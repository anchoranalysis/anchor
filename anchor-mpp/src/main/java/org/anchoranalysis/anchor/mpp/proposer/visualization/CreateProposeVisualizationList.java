/* (C)2020 */
package org.anchoranalysis.anchor.mpp.proposer.visualization;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.anchor.mpp.cfg.ColoredCfg;

public class CreateProposeVisualizationList implements CreateProposalVisualization {

    private List<CreateProposalVisualization> list = new ArrayList<>();

    public boolean add(CreateProposalVisualization e) {
        return list.add(e);
    }

    @Override
    public void addToCfg(ColoredCfg cfg) {
        for (CreateProposalVisualization item : list) {
            item.addToCfg(cfg);
        }
    }
}
