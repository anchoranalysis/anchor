/* (C)2020 */
package org.anchoranalysis.annotation.io.assignment;

import lombok.Getter;
import lombok.Setter;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.object.ObjectCollection;

public class ObjectCollectionDistanceMatrix {

    @Getter @Setter private ObjectCollection objects1;

    @Getter @Setter private ObjectCollection objects2;

    /** A two-dimensional array mapping objects1 to objects2 */
    @Getter @Setter private double[][] distanceMatrix;

    public ObjectCollectionDistanceMatrix(
            ObjectCollection objects1, ObjectCollection objects2, double[][] distanceArr)
            throws CreateException {
        super();

        this.objects1 = objects1;
        this.objects2 = objects2;
        this.distanceMatrix = distanceArr;

        if (objects1.isEmpty()) {
            throw new CreateException("objects1 must be non-empty");
        }

        if (objects2.isEmpty()) {
            throw new CreateException("objects2 must be non-empty");
        }

        if ((distanceArr.length != objects1.size()) || distanceArr[0].length != objects2.size()) {
            throw new CreateException(
                    "The distance-array has incorrect dimensions to match the objects");
        }
    }

    public double getDistance(int indx1, int indx2) {
        return distanceMatrix[indx1][indx2];
    }

    public int sizeObjects1() {
        return objects1.size();
    }

    public int sizeObjects2() {
        return objects2.size();
    }
}
