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
import org.anchoranalysis.annotation.AnnotationWithMarks;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.mpp.bean.regionmap.RegionMapSingleton;
import org.anchoranalysis.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.mpp.mark.MarkCollection;

/**
 * An annotation that consists of two sets of marks (accepted marks, and rejected marks) as well as
 * a possible reason for rejecting the entire image
 *
 * @author Owen Feehan
 */
public class MarkAnnotation extends AnnotationWithMarks {

    @Getter private boolean accepted = false;
    @Getter private RejectionReason rejectionReason;

    /** Marks in annotation */
    private MarkCollection marks;

    /** Marks covering area that should be rejected, can be NULL */
    @Getter private MarkCollection marksReject;

    /** Number of seconds since the UNIX epoch */
    @Getter private Date timeAnnotationLastUpdated;

    @Getter private boolean finished = false;

    // Hard-coded regionID
    private int regionID = GlobalRegionIdentifiers.SUBMARK_INSIDE;

    public void markAccepted(MarkCollection marks, MarkCollection marksReject) {
        finished = true;
        accepted = true;
        this.marks = marks;
        this.marksReject = marksReject;
        recordCurrentTime();
    }

    public void markPaused(MarkCollection marks, MarkCollection marksReject) {
        finished = false;
        accepted = true;
        this.marks = marks;
        this.marksReject = marksReject;
        recordCurrentTime();
    }

    // Marks the image as a whole as being rejected
    // We record the so far accepted/rejected configurations in case we change our mind lafter
    public void markRejected(
            MarkCollection marks, MarkCollection marksReject, RejectionReason reason) {
        accepted = false;
        finished = true;
        this.marks = marks;
        this.marksReject = marksReject;
        recordCurrentTime();
        this.rejectionReason = reason;
    }

    private void recordCurrentTime() {
        timeAnnotationLastUpdated = Calendar.getInstance().getTime();
    }

    @Override
    public MarkCollection getMarks() {
        return marks;
    }

    @Override
    public RegionMap getRegionMap() {
        return RegionMapSingleton.instance();
    }

    @Override
    public int getRegionID() {
        return regionID;
    }

    public void scaleXY(double scaleFactor) throws OptionalOperationUnsupportedException {
        marks.scaleXY(scaleFactor);
        marksReject.scaleXY(scaleFactor);
    }
}
