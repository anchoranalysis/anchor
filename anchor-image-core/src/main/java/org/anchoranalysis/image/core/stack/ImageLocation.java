package org.anchoranalysis.image.core.stack;

import lombok.Value;

/** 
 * The GPS coordinates associated with an image.
 */
@Value
public class ImageLocation {

    /** the latitude, in degrees (can be positive or negative). */
    private double latitude;

    /** the longitude, in degrees (can be positive or negative). */
    private double longitude;
}
