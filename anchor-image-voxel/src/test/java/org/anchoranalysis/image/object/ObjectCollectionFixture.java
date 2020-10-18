/*-
 * #%L
 * anchor-image
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
package org.anchoranalysis.image.object;

import static org.anchoranalysis.image.object.ObjectMaskFixture.DEPTH;
import static org.anchoranalysis.image.object.ObjectMaskFixture.HEIGHT;
import static org.anchoranalysis.image.object.ObjectMaskFixture.WIDTH;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.image.voxel.object.ObjectCollection;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.object.factory.ObjectCollectionFactory;
import org.anchoranalysis.spatial.extent.Extent;
import org.anchoranalysis.spatial.point.Point3i;

public class ObjectCollectionFixture {

    @Getter private final int numberNonOverlappingObjects;
    @Getter private final int numberOverlappingObjects;
    private final int numberObjects;

    @Getter @Setter private boolean do3D = false;

    /** The x-direction is always incremented, should the y-dimension also be incremented? */
    private final boolean incrementY;

    /**
     * Used as a positive between non-overlapping objects, or as a negative shift between
     * overlapping objects
     */
    private final int distanceBetweenShift;

    public ObjectCollectionFixture() {
        this(5, 3, 10, true);
    }

    public ObjectCollectionFixture(
            int numberNonOverlappingObjects,
            int numberOverlappingObjects,
            int distanceBetweenShift,
            boolean incrementY) {
        this.numberNonOverlappingObjects = numberNonOverlappingObjects;
        this.numberOverlappingObjects = numberOverlappingObjects;
        this.distanceBetweenShift = distanceBetweenShift;
        this.numberObjects = numberNonOverlappingObjects + numberOverlappingObjects;
        this.incrementY = incrementY;
    }

    public Extent extentLargerThanAllObjects() {
        int heightAllObjects =
                incrementY ? numberObjects * (HEIGHT + distanceBetweenShift) : HEIGHT;
        int depthAllObjects = do3D ? DEPTH : 1;
        return new Extent(
                numberObjects * (WIDTH + distanceBetweenShift), heightAllObjects, depthAllObjects);
    }

    public ObjectCollection createObjects(boolean removeCorners) {
        return createObjects(new ObjectMaskFixture(removeCorners, do3D));
    }

    private ObjectCollection createObjects(ObjectMaskFixture fixture) {
        Point3i running = new Point3i();
        return ObjectCollectionFactory.of(
                generateObjectsAndIncrementRunning(
                        numberNonOverlappingObjects, distanceBetweenShift, running, fixture),
                generateObjectsAndIncrementRunning(
                        numberOverlappingObjects, -distanceBetweenShift, running, fixture));
    }

    private ObjectCollection generateObjectsAndIncrementRunning(
            int numberObjects, int shift, Point3i running, ObjectMaskFixture fixture) {
        return ObjectCollectionFactory.fromRepeated(
                numberObjects,
                () -> {
                    ObjectMask object = fixture.filledMask(running.x(), running.y());
                    running.incrementX(WIDTH + shift);
                    if (incrementY) {
                        running.incrementY(HEIGHT + shift);
                    }
                    return object;
                });
    }
}
