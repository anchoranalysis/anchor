/* (C)2020 */
package org.anchoranalysis.mpp.sgmn.kernel.proposer;

import java.io.Serializable;
import lombok.Getter;
import org.anchoranalysis.anchor.mpp.proposer.error.ProposerFailureDescription;
import org.anchoranalysis.core.index.IIndexGetter;

public class KernelIterDescription implements Serializable, IIndexGetter {

    /** */
    private static final long serialVersionUID = -5135255409310941727L;

    @Getter private final int id;

    @Getter private final String description;

    @Getter private final boolean accepted;

    @Getter private final boolean proposed;

    @Getter private final int[] changedMarkIDArr;

    @Getter private final transient ProposerFailureDescription noProposalReason;

    @Getter private final long executionTime;

    private int iter;

    public KernelIterDescription(
            KernelWithID<?> kernelWithID,
            boolean accepted,
            boolean proposed,
            int[] changedMarkIDArr,
            long executionTime,
            int iter,
            ProposerFailureDescription noProposalReason) {

        this.id = kernelWithID.getID();
        this.description = kernelWithID.getDescription();
        this.accepted = accepted;
        this.proposed = proposed;
        this.changedMarkIDArr = changedMarkIDArr;
        this.executionTime = executionTime;
        this.iter = iter;
        this.noProposalReason = noProposalReason;
    }

    @Override
    public int getIndex() {
        return iter;
    }
}
