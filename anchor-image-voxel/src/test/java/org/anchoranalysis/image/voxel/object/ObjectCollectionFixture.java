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
package org.anchoranalysis.image.voxel.object;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.spatial.Extent;
import org.anchoranalysis.spatial.point.Point3i;

/**
 * Creates a {@link ObjectCollection} to be used in tests with a configurable number of overlapping
 * and non-overlapping objects.
 *
 * <p>The created objects are rectangular, with optionally corners removed.
 *
 * @author Owen Feehan
 */
public class ObjectCollectionFixture {

    /** Default number of objects that are initially created that <b>do not overlap</b>? */
    public static final int DEFAULT_NUMBER_NON_OVERLAPPING = 5;

    /** Default number of objects are subsequently created that <b>do overlap</b>? */
    public static final int DEFAULT_NUMBER_OVERLAPPING = 3;

    /**
     * Default number of pixels between objects.
     *
     * <p>Used as a positive between non-overlapping objects, or as a negative shift between
     * overlapping objects - in both X and Y directions.
     */
    public static final int DEFAULT_DISTANCE_BETWEEN_SHIFT = 10;

    /** How many objects are initially created that <b>do not overlap</b>? */
    @Getter private final int numberNonOverlapping;

    /** How many objects are subsequently created that <b>do overlap</b>? */
    @Getter private final int numberOverlapping;

    /** The total number of objects that is created, sum of non-overlapping and overlapping. */
    private final int totalNumber;

    /**
     * Number of pixels between objects.
     *
     * <p>Used as a positive between non-overlapping objects, or as a negative shift between
     * overlapping objects - in both X and Y directions.
     */
    private final int distanceBetweenShift;

    /** Whether the y-dimension also be incremented? The x-direction is always incremented. */
    private final boolean incrementY;

    /**
     * Whether to include a Z dimension to create 3D objects, or otherwise remain only in the XY
     * plane?
     */
    @Setter private boolean useZ = false;

    /**
     * Creates with a default number of non-overlapping and overlapping objects.
     *
     * <p>No Z dimension is used.
     *
     * <p>The shift between objects increments in the Y dimension.
     */
    public ObjectCollectionFixture() {
        this(
                DEFAULT_NUMBER_NON_OVERLAPPING,
                DEFAULT_NUMBER_OVERLAPPING,
                DEFAULT_DISTANCE_BETWEEN_SHIFT,
                true);
    }

    /**
     * Creates with a specific numbers of objects and distances between them.
     *
     * @param numberNonOverlapping how many objects are initially created that <b>do not
     *     overlap</b>?
     * @param numberOverlapping how many objects are subsequently created that <b>do overlap</b>?
     */
    public ObjectCollectionFixture(int numberNonOverlapping, int numberOverlapping) {
        this(numberNonOverlapping, numberOverlapping, DEFAULT_DISTANCE_BETWEEN_SHIFT, true);
    }

    /**
     * Creates with a specific numbers of objects and distances between them.
     *
     * @param numberNonOverlapping how many objects are initially created that <b>do not
     *     overlap</b>?
     * @param numberOverlapping how many objects are subsequently created that <b>do overlap</b>?
     * @param distanceBetweenShift number of pixels between objects (positive betwen non-overlapping
     *     objects, negative between overlapping) in both X and Y directions.
     * @param incrementY whether the y-dimension also be incremented? The x-direction is always
     *     incremented.
     */
    public ObjectCollectionFixture(
            int numberNonOverlapping,
            int numberOverlapping,
            int distanceBetweenShift,
            boolean incrementY) {
        this.numberNonOverlapping = numberNonOverlapping;
        this.numberOverlapping = numberOverlapping;
        this.distanceBetweenShift = distanceBetweenShift;
        this.incrementY = incrementY;

        this.totalNumber = numberNonOverlapping + numberOverlapping;
    }

    /**
     * Creates the {@link ObjectCollection} from the fixture.
     *
     * @param removeCorners whether to remove single-voxel pixels from corners or not?
     * @return a newly created collection of newly created objects.
     */
    public ObjectCollection createObjects(boolean removeCorners) {
        return createObjects(new ObjectMaskFixture(removeCorners, useZ));
    }

    /**
     * An {@link Extent} that is guaranteed to contain all objects that would created with {@link
     * #createObjects}.
     *
     * @return a newly created {@link Extent}.
     */
    public Extent extentLargerThanAllObjects() {
        ObjectMaskFixture fixture = new ObjectMaskFixture(true, useZ);
        int heightSingleObject = fixture.extent().y();
        int heightAllObjects =
                incrementY
                        ? totalNumber * (heightSingleObject + distanceBetweenShift)
                        : heightSingleObject;
        int depthAllObjects = useZ ? fixture.extent().z() : 1;
        return new Extent(
                totalNumber * (fixture.extent().x() + distanceBetweenShift),
                heightAllObjects,
                depthAllObjects);
    }

    /** Creates objects given a {@link ObjectMaskFixture} for creating single objects. */
    private ObjectCollection createObjects(ObjectMaskFixture fixture) {
        Point3i running = new Point3i();
        return ObjectCollectionFactory.of(
                createIncrementingObjects(
                        numberNonOverlapping, distanceBetweenShift, running, fixture),
                createIncrementingObjects(
                        numberOverlapping, -distanceBetweenShift, running, fixture));
    }

    /**
     * Creates the objects with incrementing X and Y location.
     *
     * @param numberObjects number of objects to create
     * @param shift how much to add to X and Y positions between each object
     * @param point the corner-point at which the initial object is created, and which is updated
     *     for each subsequent object created.
     * @param fixture the fixture used to create a single object.
     */
    private ObjectCollection createIncrementingObjects(
            int numberObjects, int shift, Point3i point, ObjectMaskFixture fixture) {
        return ObjectCollectionFactory.fromRepeated(
                numberObjects,
                () -> {
                    ObjectMask object = fixture.filledMask(point.x(), point.y());
                    point.incrementX(fixture.extent().x() + shift);
                    if (incrementY) {
                        point.incrementY(fixture.extent().y() + shift);
                    }
                    return object;
                });
    }
}
