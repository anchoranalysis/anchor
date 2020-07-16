/*-
 * #%L
 * anchor-mpp
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

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
