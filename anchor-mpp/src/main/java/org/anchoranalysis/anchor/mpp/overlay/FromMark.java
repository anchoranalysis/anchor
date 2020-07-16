/* (C)2020 */
package org.anchoranalysis.anchor.mpp.overlay;

import org.anchoranalysis.anchor.mpp.bean.regionmap.RegionMembershipWithFlags;
import org.anchoranalysis.anchor.mpp.mark.Mark;
import org.anchoranalysis.anchor.overlay.object.scaled.ScaledMaskCreator;
import org.anchoranalysis.anchor.overlay.writer.DrawOverlay;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.properties.ObjectWithProperties;

class FromMark implements ScaledMaskCreator {

    private RegionMembershipWithFlags regionMembership;

    public FromMark(RegionMembershipWithFlags regionMembership) {
        super();
        this.regionMembership = regionMembership;
    }

    @Override
    public ObjectWithProperties createScaledMask(
            DrawOverlay overlayWriter,
            ObjectWithProperties omUnscaled,
            double scaleFactor,
            Object originalObject,
            ImageDimensions sdScaled,
            BinaryValuesByte bv)
            throws CreateException {

        Mark originalMark = (Mark) originalObject;

        ObjectWithProperties omScaled =
                originalMark.calcMaskScaledXY(sdScaled, regionMembership, bv, scaleFactor);

        // We keep the properties the same
        return new ObjectWithProperties(omScaled.getMask(), omUnscaled.getProperties());
    }
}
