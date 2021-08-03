/*-
 * #%L
 * anchor-plugin-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.test.image;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Optional;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.mask.Mask;
import org.anchoranalysis.image.voxel.binary.BinaryVoxelsFactory;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Creates two or three-dimensional {@link Mask}s for tests.
 * 
 * <p>The mask has an area of <i>on</i> pixels that is rectangle (2D) or a cuboid (3D)
 * and otherwise pixels are <i>off</i>.
 *
 * @author Owen Feehan
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaskFixture {

    /** 
     * Default corner used for box of <i>on</i> voxels in 3D.
     *
     * <p>The X and Y dimensions are used in 2D.
     */
    private static final Point3i DEFAULT_CORNER = new Point3i(14,2,2);
    
    /** 
     * The extent of the created box of <i>on</i> voxels in 3D.
     *
     * <p>The X and Y dimensions are used in 2D.
     */
    private static final Point3i BOX_EXTENT = new Point3i(40,7,3);

    /**
     * Creates the {@link Mask} with <b>the default corner and no assigned resolution</b>.
     * 
     * @param do3D if true, a 3D mask is created, otherwise 2D.
     * @return the mask
     */
    public static Mask create(boolean do3D) {
        return create(DEFAULT_CORNER, do3D);
    }

    /**
     * Creates the {@link Mask} with <b>the default corner and a particular resolution</b>.
     * 
     * @param do3D if true, a 3D mask is created, otherwise 2D.
     * @param resolution the resolution to assign to the mask
     * @return the mask
     */
    public static Mask create(boolean do3D, Optional<Resolution> resolution) {
        return create(DEFAULT_CORNER, do3D, resolution);
    }
    
    /**
     * Creates the {@link Mask} with <b>a particular corner and no assigned resolution</b>.
     * 
     * @param corner the corner
     * @param do3D if true, a 3D mask is created, otherwise 2D.
     * @return the mask
     */
    public static Mask create(Point3i corner, boolean do3D) {
        return create(corner, do3D, Optional.empty());
    }
    
    /**
     * Creates the {@link Mask} with <b>a particular corner and a particular resolution</b>.
     * 
     * @param corner the corner
     * @param do3D if true, a 3D mask is created, otherwise 2D.
     * @return the mask
     */
    public static Mask create(Point3i corner, boolean do3D, Optional<Resolution> resolution) {
        Mask mask = new Mask(BinaryVoxelsFactory.createEmptyOff(maskExtent(do3D)), resolution);
        mask.assignOn().toBox(createRectange(corner, do3D));
        return mask;
    }

    public static Extent maskExtent(boolean do3D) {
        return do3D ? ChannelFixture.MEDIUM_3D : ChannelFixture.MEDIUM_2D;
    }
    
    public static int width() {
        return BOX_EXTENT.x();
    }
    
    public static int height() {
        return BOX_EXTENT.y();
    }

    public static int depth(boolean do3D) {
        return do3D ? BOX_EXTENT.z() : 1;
    }

    /** Creates a rectangle (2D) or cuboid (3D) object-mask of ON pixels. */
    private static BoundingBox createRectange(Point3i corner, boolean do3D) {
        Extent extentCorrected = Extent.createFromTupleReuse(maybeSuppressZ(BOX_EXTENT, do3D, 1));
        Point3i cornerCorrected = maybeSuppressZ(corner, do3D, 0);
        return new BoundingBox(cornerCorrected, extentCorrected);
    }
    
    /** Suppresses the Z dimension of a point when operating in 2D. */
    private static Point3i maybeSuppressZ(Point3i point, boolean do3D, int suppressedValue) {
        if (do3D)  {
            return point;
        } else {
            return new Point3i(point.x(), point.y(), suppressedValue);
        }
    }
}
