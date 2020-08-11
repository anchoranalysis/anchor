package org.anchoranalysis.image;

import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public class BoundingBoxFixture {
    
    /**
     * Short-hand factory method for creating a bounding-box, where each dimension is uniform 
     * 
     * @param corner left-corner in every dimension
     * @param extent extent in every dimension
     * @return the newly created bounding-box
     */
    public static BoundingBox of(int corner, int extent) {
        return of(corner, corner, corner, extent, extent, extent);
    }
    
    /**
     * Short-hand factory method for creating a bounding-box 
     * 
     * @param x left-corner in x-dimension
     * @param y left-corner in y-dimension
     * @param z left-corner in z-dimension
     * @param width bounding-box width (extent in x-dimension)
     * @param height bounding-box width (extent in y-dimension)
     * @param depth bounding-box width (extent in z-dimension)
     * @return the newly created bounding-box
     */
    public static BoundingBox of(int x, int y, int z, int width, int height, int depth) {
        return new BoundingBox(new Point3i(x, y, z), new Extent(width, height, depth));
    }
}
