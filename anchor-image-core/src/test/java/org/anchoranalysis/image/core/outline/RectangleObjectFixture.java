package org.anchoranalysis.image.core.outline;

import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Creates a rectangular object that doesn't lie at the edges.
 * 
 * <p>A margin is left on each side of a certain size.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class RectangleObjectFixture {

    /** Width of scene. */
    private static final int SCENE_WIDTH = 20;
    
    /** Height of scene. */
    private static final int SCENE_HEIGHT = 15;
    
    /** Depth of scene. */
    private static final int SCENE_DEPTH = 7;
    
    /** The space between the scene and the rectangle in all dimensions. */
    private static final int MARGIN = 2;

    /** Width of rectangle. */
    public static final int RECTANGLE_WIDTH = subtractTwoMargins(SCENE_WIDTH);
    
    /** Height of rectangle. */
    public static final int RECTANGLE_HEIGHT = subtractTwoMargins(SCENE_HEIGHT);
    
    /** Depth of rectangle. */
    public static final int RECTANGLE_DEPTH = subtractTwoMargins(SCENE_DEPTH);
    
    public static ObjectMask create(boolean useZ) {
        Point3i corner = new Point3i(MARGIN, MARGIN, useZ ? MARGIN : 1);
        Extent reducedExtent = new Extent( subtractTwoMargins(SCENE_WIDTH), subtractTwoMargins(SCENE_HEIGHT), useZ ? subtractTwoMargins(SCENE_DEPTH) : 1 );
        return createRectangle(corner, reducedExtent);
    }
        
    private static int subtractTwoMargins(int fullExtent) {
        return fullExtent - (2*MARGIN);
    }
    
    private static ObjectMask createRectangle( Point3i corner, Extent extent ) {
        ObjectMask object = new ObjectMask( new BoundingBox(corner, extent) );
        object.assignOn().toAll();
        return object;
    }
}
