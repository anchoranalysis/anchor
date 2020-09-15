/*-
 * #%L
 * anchor-annotation
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

package org.anchoranalysis.annotation.mark;

import java.util.Calendar;
import java.util.Date;
import lombok.Getter;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.MarkCollection;

/**
 * An annotation that consists of two sets of marks, accepted and rejected.
 *
 * <p>It also contains a possible reason for rejecting the entire image.
 *
 * @author Owen Feehan
 * @param <T> rejection-reason
 */
public class DualMarksAnnotation<T> implements AnnotationWithMarks {

    @Getter private boolean accepted = false;
    @Getter private T rejectionReason;

    /** Marks in annotation */
    private MarkCollection marks;

    /** Marks covering area that should be rejected, can be null */
    @Getter private MarkCollection marksReject;

    /** Number of seconds since the UNIX epoch */
    @Getter private Date timeAnnotationLastUpdated;

    @Getter private boolean finished = false;

    // Hard-coded regionID
    private int regionID = GlobalRegionIdentifiers.SUBMARK_INSIDE;

    /**
     * Assigns marks with an overall <i>accepted</i> state.
     *
     * @param dualMark the marks to assign to the annotation.
     */
    public void assignAccepted(DualMarks dualMark) {
        finished = true;
        accepted = true;
        assign(dualMark);
    }

    /**
     * Assigns marks with an overall <i>paused</i> state.
     *
     * @param dualMark the marks to assign to the annotation.
     */
    public void assignPaused(DualMarks dualMark) {
        finished = false;
        accepted = true;
        assign(dualMark);
    }

    /**
     * Assigns marks with an overall <i>rejected</i> state.
     *
     * <p>The so far accepted/rejected marks are still stored in case there's a later change of
     * mind.
     *
     * @param dualMark the marks to assign to the annotation.
     * @param reason the reason for rejection
     */
    public void assignRejected(DualMarks dualMark, T reason) {
        accepted = false;
        finished = true;
        assign(dualMark);
        this.rejectionReason = reason;
    }

    private void recordCurrentTime() {
        timeAnnotationLastUpdated = Calendar.getInstance().getTime();
    }

    @Override
    public MarkCollection marks() {
        return marks;
    }

    /**
     * Scales the marks in the annotation in X and Y dimensions.
     *
     * @param scaleFactor how much to scale by
     * @throws OptionalOperationUnsupportedException if the type of mark used in the annotation does
     *     not supported scaling.
     */
    public void scaleXY(double scaleFactor) throws OptionalOperationUnsupportedException {
        marks.scaleXY(scaleFactor);
        marksReject.scaleXY(scaleFactor);
    }

    @Override
    public RegionMembershipWithFlags region() {
        return RegionMapSingleton.instance().membershipWithFlagsForIndex(regionID);
    }

    private void assign(DualMarks dualMark) {
        this.marks = dualMark.accepted();
        this.marksReject = dualMark.rejected();
        recordCurrentTime();
    }
}
