package org.anchoranalysis.image.object;

import static org.anchoranalysis.image.object.ObjectMaskFixture.DEPTH;
import static org.anchoranalysis.image.object.ObjectMaskFixture.HEIGHT;
import static org.anchoranalysis.image.object.ObjectMaskFixture.WIDTH;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.factory.ObjectCollectionFactory;

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
