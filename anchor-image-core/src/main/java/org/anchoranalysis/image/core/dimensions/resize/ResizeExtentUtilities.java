package org.anchoranalysis.image.core.dimensions.resize;

import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.scale.ScaleFactor;
import org.anchoranalysis.spatial.scale.ScaleFactorUtilities;
import lombok.AccessLevel;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class ResizeExtentUtilities {

    /**
     * Multiplexes between {@link #relativeScalePreserveAspectRatio(Extent, Extent)} and {@link #relativeScale(Extent, Extent)}.
     * 
     * @param source source extent (only X and Y dimensions are considered)
     * @param target target extent (only X and Y dimensions are considered)
     * @param preserveAspectRatio iff true, the aspect ratio is preserved, and {@link #relativeScalePreserveAspectRatio(Extent, Extent)} is called, otherwise {@link #relativeScale(Extent, Extent)}. 
     * @return
     */
    public static ScaleFactor relativeScale(Extent source, Extent target, boolean preserveAspectRatio) {
        if (preserveAspectRatio) {
            return ResizeExtentUtilities.relativeScalePreserveAspectRatio(source, target);
        } else {
            return ResizeExtentUtilities.relativeScale(source, target);
        }
    }
    
    /**
     * Calculates a scaling factor so as to maximally scale {@code source} to {@code target} -
     * <b>while preserving the aspect ratio</b>.
     *
     * <p>Either the X or Y dimension is guaranteed to have a scale-factor {@code target / source},
     * and the other will scale so as not to exceed the size of {@code target}.
     *
     * @param source source extent (only X and Y dimensions are considered)
     * @param target target extent (only X and Y dimensions are considered)
     * @return the scaling-factor to scale the source to be the same size as the target
     */
    public static ScaleFactor relativeScalePreserveAspectRatio(Extent source, Extent target) {
        ScaleFactor withoutPreserving = relativeScale(source, target);
        double minDimension = withoutPreserving.minimumDimension();
        return new ScaleFactor(minDimension, minDimension);
    }
    
    /**
     * Calculates a scaling factor so as to scale {@code source} to {@code target}.
     *
     * <p>i.e. the scale-factor is {@code target / source} for each XY dimension.
     *
     * @param source source extent (only X and Y dimensions are considered)
     * @param target target extent (only X and Y dimensions are considered)
     * @return the scaling-factor to scale the source to be the same size as the target
     */
    public static ScaleFactor relativeScale(Extent source, Extent target) {
        return new ScaleFactor(
                ScaleFactorUtilities.deriveScalingFactor(target.x(), source.x()),
                ScaleFactorUtilities.deriveScalingFactor(target.y(), source.y()));
    }
}
