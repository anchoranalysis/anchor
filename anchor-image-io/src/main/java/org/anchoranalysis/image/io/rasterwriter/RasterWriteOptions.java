package org.anchoranalysis.image.io.rasterwriter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Options describing rasters which may determine which writer is used.
 *
 * <p>This class is <i>immutable</i>.
 * 
 * @author Owen Feehan
 *
 */
@Value @AllArgsConstructor(access=AccessLevel.PRIVATE)
public class RasterWriteOptions {

    private static final RasterWriteOptions RGB_ALWAYS_2D = rgb(true);
    
    private static final RasterWriteOptions RGB_MAYBE_3D = rgb(false);
    
    private static final RasterWriteOptions ALWAYS_SINGLE_CHANNEL_MAYBE_3D = alwaysOneOrThreeChannels(false);
    
    /** True the output is guaranteed to only ever 2D i.e. maximally one z-slice? */
    private boolean always2D;
    
    /** The number of channels is guaranteed to be 1 or 3 in the output. */
    private boolean alwaysOneOrThreeChannels;

    /*** Whether it's an RGB image (exactly three channels visualzied jointly, rather than independently) */
    private boolean rgb;
    
    /**
     * Combines with another {@link RasterWriteOptions} by performing a logical <i>and</i> on each field.
     * 
     * @param other the other {@link RasterWriteOptions} to combine with.
     * @return a newly created {@link RasterWriteOptions} where each field is the logical and of the two inputs
     */
    public RasterWriteOptions and( RasterWriteOptions other ) {
        return new RasterWriteOptions(
            always2D && other.always2D,
            alwaysOneOrThreeChannels && other.alwaysOneOrThreeChannels,
            rgb && other.rgb
        );
    }
    
    /**
     * Creates a copy of the {@link RasterWriteOptions} which will always be 2D.
     * 
     * @return a newly created {@link RasterWriteOptions} with identical fields, except {@code always2D} is true.
     */
    public RasterWriteOptions always2D() {
        return new RasterWriteOptions(true, alwaysOneOrThreeChannels, rgb);
    }
    
    public static RasterWriteOptions rgbAlways2D() {
        return RGB_ALWAYS_2D;
    }
    
    public static RasterWriteOptions rgbMaybe3D() {
        return RGB_MAYBE_3D;
    }

    public static RasterWriteOptions binaryChannelMaybe3D() {
        return singleChannelMaybe3D();
    }
    
    public static RasterWriteOptions singleChannelMaybe3D() {
        return ALWAYS_SINGLE_CHANNEL_MAYBE_3D;
    }
    
    public static RasterWriteOptions alwaysOneOrThreeChannels(boolean always2D) {
        return new RasterWriteOptions(always2D, true, false);
    }
    
    public static RasterWriteOptions maybeRGB(boolean rgb) {
        if (rgb) {
            return RGB_MAYBE_3D;
        } else {
            return new RasterWriteOptions(false, false, false);
        }
    }
        
    private static RasterWriteOptions rgb(boolean always2D) {
        return new RasterWriteOptions(always2D, true, true);
    }
}
