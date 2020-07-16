/* (C)2020 */
package org.anchoranalysis.mpp.io.cfg;

import org.anchoranalysis.anchor.mpp.cfg.Cfg;
import org.anchoranalysis.image.stack.DisplayStack;

public class CfgWithDisplayStack {

    private Cfg cfg;
    private DisplayStack stack;

    public CfgWithDisplayStack(Cfg cfg, DisplayStack stack) {
        super();
        this.cfg = cfg;
        this.stack = stack;
    }

    public Cfg getCfg() {
        return cfg;
    }

    public void setCfg(Cfg cfg) {
        this.cfg = cfg;
    }

    public DisplayStack getStack() {
        return stack;
    }

    public void setStack(DisplayStack stack) {
        this.stack = stack;
    }
}
