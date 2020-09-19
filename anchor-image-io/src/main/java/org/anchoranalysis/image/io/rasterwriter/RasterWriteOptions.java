package org.anchoranalysis.image.io.rasterwriter;

import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * Options describing rasters which may determine which writer is used.
 * 
 * @author Owen Feehan
 *
 */
@Value @AllArgsConstructor
public class RasterWriteOptions {

    /** True the output is guaranteed to only ever 2D i.e. maximally one z-slice? */
    private final boolean always2D;
    
    /** The number of channels expected in the output. */
    private final int numberChannels;
}
