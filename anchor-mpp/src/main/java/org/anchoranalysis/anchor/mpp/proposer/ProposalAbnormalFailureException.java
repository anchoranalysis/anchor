/* (C)2020 */
package org.anchoranalysis.anchor.mpp.proposer;

import org.anchoranalysis.core.error.friendly.AnchorFriendlyCheckedException;

/**
 * An exception when a proposal could not be made for an abnormal reason
 *
 * <p>This is different from the usual failure to make a proposal (return value of null), which
 * happens frequently as an ordinary part of proposal routines
 *
 * @author Owen Feehan
 */
public class ProposalAbnormalFailureException extends AnchorFriendlyCheckedException {

    /** */
    private static final long serialVersionUID = 1L;

    public ProposalAbnormalFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProposalAbnormalFailureException(String message) {
        super(message);
    }

    public ProposalAbnormalFailureException(Throwable cause) {
        super(cause);
    }
}
