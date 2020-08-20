/*-
 * #%L
 * anchor-mpp
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

package org.anchoranalysis.anchor.mpp.probmap;

import java.util.Optional;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.ops.MaskFromObjects;

public class ProbMapObjectCollection implements ProbMap {

    private final ObjectCollection objects;
    private final Dimensions dimensions;

    private final ProbWeights probWeights;

    public ProbMapObjectCollection(ObjectCollection objects, Dimensions dim) {
        super();
        this.objects = objects;
        this.dimensions = dim;

        probWeights = new ProbWeights();
        for (ObjectMask objectMask : objects) {
            probWeights.add((double) objectMask.binaryVoxels().countOn());
        }
    }

    @Override
    public Optional<Point3d> sample(RandomNumberGenerator randomNumberGenerator) {

        if (probWeights.size() == 0) {
            return Optional.empty();
        }

        // We want to sample objects (weighted by the number of ON pixels)
        int index = probWeights.sample(randomNumberGenerator);

        assert (index >= 0);

        ObjectMask object = objects.get(index);
        return Optional.of(sampleFromObject(object, randomNumberGenerator));
    }

    @Override
    public Dimensions dimensions() {
        return dimensions;
    }

    @Override
    public Mask visualization() throws OptionalOperationUnsupportedException {
        return MaskFromObjects.createFromObjects(objects, dimensions, BinaryValues.getDefault());
    }

    private Point3d sampleFromObject(
            ObjectMask object, RandomNumberGenerator randomNumberGenerator) {

        // Now we keep picking a pixel at random from the object-mask until we find one that is
        //  on.  Could be very inefficient for low-density bounding boxes? So we should make sure
        //  bounding boxes are tight

        Extent extent = object.extent();
        long vol = extent.calculateVolume();
        int volXY = extent.volumeXY();
        int exY = extent.x();

        while (true) {

            long index3D = randomNumberGenerator.sampleLongFromRange(vol);

            int slice = (int) (index3D / volXY);
            int index2D = (int) (index3D % volXY);

            byte b = object.sliceBufferLocal(slice).get(index2D);
            if (b == object.binaryValuesByte().getOnByte()) {

                int xRel = index2D % exY;
                int yRel = index2D / exY;
                int zRel = slice;

                int x = xRel + object.boundingBox().cornerMin().x();
                int y = yRel + object.boundingBox().cornerMin().y();
                int z = zRel + object.boundingBox().cornerMin().z();

                return new Point3d(x, y, z);
            }
        }
    }
}
