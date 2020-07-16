/* (C)2020 */
package org.anchoranalysis.anchor.mpp.feature.nrg.cfg;

import java.io.Serializable;
import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.anchor.mpp.feature.nrg.scheme.NRGSchemeWithSharedFeatures;
import org.anchoranalysis.anchor.mpp.pxlmark.memo.VoxelizedMarkMemo;

public class CfgWithNRGTotal implements Serializable {

    /** */
    private static final long serialVersionUID = -7008599104878130986L;

    // Pre-Annealed
    private double nrgTotal;

    // Associated configuration
    private Cfg cfg;

    // Associated NRG Scheme, which should include the NRGSavedPairs
    private transient NRGSchemeWithSharedFeatures nrgScheme;

    public CfgWithNRGTotal(Cfg cfg, NRGSchemeWithSharedFeatures nrgScheme) {
        this.cfg = cfg;
        this.nrgScheme = nrgScheme;

        this.nrgTotal = 0;
    }

    public CfgWithNRGTotal shallowCopy() {
        CfgWithNRGTotal out = new CfgWithNRGTotal(this.cfg, this.nrgScheme);
        out.nrgTotal = this.nrgTotal;
        return out;
    }

    public CfgWithNRGTotal deepCopy() {
        CfgWithNRGTotal out = new CfgWithNRGTotal(this.cfg.deepCopy(), this.nrgScheme);
        out.nrgTotal = this.nrgTotal;
        return out;
    }

    public CfgWithNRGTotal deepCopyCfgShallowRest() {

        Cfg newCfg = this.cfg.deepCopy();

        CfgWithNRGTotal out = new CfgWithNRGTotal(newCfg, this.nrgScheme);
        out.nrgTotal = this.nrgTotal;
        return out;
    }

    public double getNrgTotal() {
        return nrgTotal;
    }

    public Cfg getCfg() {
        return cfg;
    }

    public NRGSchemeWithSharedFeatures getNrgScheme() {
        return nrgScheme;
    }

    public void add(VoxelizedMarkMemo newPxlMarkMemo) {
        Cfg newCfg = this.cfg.shallowCopy();
        newCfg.add(newPxlMarkMemo.getMark());
        // We adopt the new configuration
        this.cfg = newCfg;
    }

    public void rmv(int index) {

        // As none of our updates involve the memo list, we can do
        //  this operate after the other remove operations
        Cfg newCfg = this.cfg.shallowCopy();
        newCfg.remove(index);

        this.cfg = newCfg;
    }

    public void setNrgTotal(double nrgTotal) {
        this.nrgTotal = nrgTotal;
    }

    // calculates a new energy and configuration based upon a mark at a particular index
    //   changing into new mark
    public void exchange(int index, VoxelizedMarkMemo newMark) {

        // We shallow copy the existing configuration
        Cfg newCfg = this.cfg.shallowCopy();
        newCfg.exchange(index, newMark.getMark());

        // We adopt the new configuration
        this.cfg = newCfg;
    }

    public void setCfg(Cfg cfg) {
        this.cfg = cfg;
    }
}
