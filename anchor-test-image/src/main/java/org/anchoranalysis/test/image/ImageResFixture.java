/* (C)2020 */
package org.anchoranalysis.test.image;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.extent.ImageResolution;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageResFixture {

    public static final ImageResolution INSTANCE = create();

    private static ImageResolution create() {
        return new ImageResolution(0.01, 0.01, 0.025);
    }
}
