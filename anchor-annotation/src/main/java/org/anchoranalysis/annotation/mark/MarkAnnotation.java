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
