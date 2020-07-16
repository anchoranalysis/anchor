/* (C)2020 */
package org.anchoranalysis.anchor.mpp.bound;

import java.util.Optional;
import org.anchoranalysis.anchor.mpp.bean.bound.ResolvedBound;

public class BidirectionalBound {

    private final Optional<ResolvedBound> forward;
    private final Optional<ResolvedBound> reverse;

    public BidirectionalBound(Optional<ResolvedBound> forward, Optional<ResolvedBound> reverse) {
        super();
        this.forward = forward;
        this.reverse = reverse;
    }

    public double getMaxOfMax() {
        if (forward.isPresent() && reverse.isPresent()) {
            return Math.max(forward.get().getMax(), reverse.get().getMax());
        }

        if (forward.isPresent()) {
            return forward.get().getMax();
        }

        if (reverse.isPresent()) {
            return reverse.get().getMax();
        }

        return Double.NaN;
    }

    @Override
    public String toString() {
        return String.format("[%s and %s]", forward, reverse);
    }

    public boolean isUnboundedAtBothEnds() {
        return !forward.isPresent() && !reverse.isPresent();
    }

    public boolean isUnbounded() {
        return !forward.isPresent() || !reverse.isPresent();
    }

    public boolean isUnboundedOrOutsideRange(double range) {
        if (isUnbounded()) {
            return true;
        }
        return getMaxOfMax() > range;
    }

    public boolean isUnboundedOrOutsideRangeAtBothEnds(double range) {

        boolean forwardUnbounded = !forward.isPresent() || forward.get().getMax() > range;
        boolean reverseUnbounded = !reverse.isPresent() || reverse.get().getMax() > range;
        return forwardUnbounded && reverseUnbounded;
    }

    public double ratioBounds() {

        if (isUnbounded()) {
            return -1;
        }

        double fwd = forward.get().getMax(); // NOSONAR
        double rvrs = reverse.get().getMax(); // NOSONAR

        double maxBoth;
        double minBoth;
        if (fwd >= rvrs) {
            maxBoth = fwd;
            minBoth = rvrs;
        } else {
            maxBoth = rvrs;
            minBoth = fwd;
        }

        return maxBoth / minBoth;
    }

    public Optional<ResolvedBound> getForward() {
        return forward;
    }

    public Optional<ResolvedBound> getReverse() {
        return reverse;
    }
}
