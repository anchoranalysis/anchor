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
/* (C)2020 */
package org.anchoranalysis.annotation.mark;

import java.util.Calendar;
import java.util.Date;
import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMap;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.annotation.AnnotationWithCfg;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;

/**
 * An annotation that consists of two sets of marks (accepted marks, and rejected marks) as well as
 * a possible reason for rejecting the entire image
 *
 * @author Owen Feehan
 */
public class MarkAnnotation extends AnnotationWithCfg {

    private boolean accepted = false;
    private RejectionReason rejectionReason;

    private Cfg cfg; // Marks in annotation
    private Cfg cfgReject; // Marks covering area that should be rejected, can be NULL

    private Date timeAnnotationLastUpdated; // Number of seconds since the UNIX epoch
    private boolean finished = false;

    // Hard-coded regionID
    private int regionID = GlobalRegionIdentifiers.SUBMARK_INSIDE;

    public void markAccepted(Cfg cfg, Cfg cfgReject) {
        finished = true;
        accepted = true;
        this.cfg = cfg;
        this.cfgReject = cfgReject;
        recordCurrentTime();
    }

    public void markPaused(Cfg cfg, Cfg cfgReject) {
        finished = false;
        accepted = true;
        this.cfg = cfg;
        this.cfgReject = cfgReject;
        recordCurrentTime();
    }

    // Marks the image as a whole as being rejected
    // We record the so far accepted/rejected configurations in case we change our mind lafter
    public void markRejected(Cfg cfg, Cfg cfgReject, RejectionReason reason) {
        accepted = false;
        finished = true;
        this.cfg = cfg;
        this.cfgReject = cfgReject;
        recordCurrentTime();
        this.rejectionReason = reason;
    }

    private void recordCurrentTime() {
        timeAnnotationLastUpdated = Calendar.getInstance().getTime();
    }

    public boolean isAccepted() {
        return accepted;
    }

    public RejectionReason getRejectionReason() {
        return rejectionReason;
    }

    @Override
    public Cfg getCfg() {
        return cfg;
    }

    public Date getTimeAnnotationLastUpdated() {
        return timeAnnotationLastUpdated;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    @Override
    public RegionMap getRegionMap() {
        return RegionMapSingleton.instance();
    }

    @Override
    public int getRegionID() {
        return regionID;
    }

    public Cfg getCfgReject() {
        return cfgReject;
    }

    public void scaleXY(double scaleFactor) throws OptionalOperationUnsupportedException {
        cfg.scaleXY(scaleFactor);
        cfgReject.scaleXY(scaleFactor);
    }
}
