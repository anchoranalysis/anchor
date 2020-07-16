/*-
 * #%L
 * anchor-annotation-io
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
