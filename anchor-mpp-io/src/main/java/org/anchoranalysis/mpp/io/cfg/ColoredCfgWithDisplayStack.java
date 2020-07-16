/* (C)2020 */
package org.anchoranalysis.mpp.io.cfg;

import org.anchoranalysis.anchor.mpp.cfg.ColoredCfg;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.core.color.ColorIndex;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.stack.DisplayStack;

public class ColoredCfgWithDisplayStack {

    private ColoredCfg cfg;
    private DisplayStack stack;

    public ColoredCfgWithDisplayStack(ColoredCfg cfg, DisplayStack stack) {
        super();
        this.cfg = cfg;
        this.stack = stack;
    }

    public ColoredCfgWithDisplayStack(
            CfgWithDisplayStack cwds, ColorIndex colorIndex, IDGetter<Mark> idGetter) {
        this.cfg = new ColoredCfg(cwds.getCfg(), colorIndex, idGetter);
        this.stack = cwds.getStack();
    }

    public ColoredCfg getCfg() {
        return cfg;
    }

    public void setCfg(ColoredCfg cfg) {
        this.cfg = cfg;
    }

    public DisplayStack getStack() {
        return stack;
    }

    public void setStack(DisplayStack stack) {
        this.stack = stack;
    }
}
