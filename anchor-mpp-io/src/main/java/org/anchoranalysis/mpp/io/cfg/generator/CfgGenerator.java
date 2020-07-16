/* (C)2020 */
package org.anchoranalysis.mpp.io.cfg.generator;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.mpp.io.cfg.ColoredCfgWithDisplayStack;

public class CfgGenerator extends CfgGeneratorBase {

    public CfgGenerator(DrawObject maskWriter, IDGetter<Overlay> idGetter) {
        this(maskWriter, null, idGetter);
    }

    public CfgGenerator(
            DrawObject maskWriter, ColoredCfgWithDisplayStack cws, IDGetter<Overlay> idGetter) {
        this(
                maskWriter,
                cws,
                idGetter,
                RegionMapSingleton.instance()
                        .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE));
    }

    public CfgGenerator(
            DrawObject maskWriter,
            ColoredCfgWithDisplayStack cws,
            IDGetter<Overlay> idGetter,
            RegionMembershipWithFlags regionMembership) {
        super(new SimpleOverlayWriter(maskWriter), cws, idGetter, regionMembership);
    }

    @Override
    protected DisplayStack background(DisplayStack stack) {
        return stack;
    }
}
