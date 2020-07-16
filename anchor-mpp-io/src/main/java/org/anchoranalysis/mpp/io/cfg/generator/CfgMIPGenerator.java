/* (C)2020 */
package org.anchoranalysis.mpp.io.cfg.generator;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.GlobalRegionIdentifiers;
import org.anchoranalysis.anchor.mpp.regionmap.RegionMapSingleton;
import org.anchoranalysis.anchor.overlay.Overlay;
import org.anchoranalysis.anchor.overlay.bean.DrawObject;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.idgetter.IDGetter;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.io.bean.object.writer.Flatten;
import org.anchoranalysis.mpp.io.cfg.ColoredCfgWithDisplayStack;

public class CfgMIPGenerator extends CfgGeneratorBase {

    // We cache the last background, and background MIP
    private DisplayStack cachedBackground;
    private DisplayStack cachedBackgroundMIP;

    public CfgMIPGenerator(DrawObject maskWriter, IDGetter<Overlay> idGetter) {
        this(
                maskWriter,
                null,
                idGetter,
                RegionMapSingleton.instance()
                        .membershipWithFlagsForIndex(GlobalRegionIdentifiers.SUBMARK_INSIDE));
    }

    public CfgMIPGenerator(
            DrawObject maskWriter,
            ColoredCfgWithDisplayStack cws,
            IDGetter<Overlay> idGetter,
            RegionMembershipWithFlags regionMembership) {
        super(createWriter(maskWriter), cws, idGetter, regionMembership);
    }

    @Override
    protected DisplayStack background(DisplayStack stack) throws OperationFailedException {
        // We avoid repeating the same calculation using a cache
        if (stack != cachedBackground) {
            cachedBackground = stack;
            cachedBackgroundMIP = stack.maxIntensityProj();
        }

        return cachedBackgroundMIP;
    }

    private static DrawOverlay createWriter(DrawObject maskWriter) {
        return new SimpleOverlayWriter(new Flatten(maskWriter));
    }
}
