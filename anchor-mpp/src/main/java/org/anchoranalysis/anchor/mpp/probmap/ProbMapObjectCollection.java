/* (C)2020 */
package org.anchoranalysis.anchor.mpp.probmap;

import java.util.Optional;
import org.anchoranalysis.core.error.OptionalOperationUnsupportedException;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.random.RandomNumberGenerator;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.binary.values.BinaryValues;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.object.ObjectCollection;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.object.ops.BinaryChnlFromObjects;

public class ProbMapObjectCollection implements ProbMap {

    private final ObjectCollection objects;
    private final ImageDimensions dimensions;

    private final ProbWeights probWeights;

    public ProbMapObjectCollection(ObjectCollection objects, ImageDimensions dim) {
        super();
        this.objects = objects;
        this.dimensions = dim;

        probWeights = new ProbWeights();
        for (ObjectMask objectMask : objects) {
            probWeights.add((double) objectMask.binaryVoxelBox().countOn());
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
    public ImageDimensions getDimensions() {
        return dimensions;
    }

    @Override
    public Mask visualization() throws OptionalOperationUnsupportedException {
        return BinaryChnlFromObjects.createFromObjects(
                objects, dimensions, BinaryValues.getDefault());
    }

    private Point3d sampleFromObject(
            ObjectMask object, RandomNumberGenerator randomNumberGenerator) {

        // Now we keep picking a pixel at random from the object mask until we find one that is
        //  on.  Could be very inefficient for low-density bounding boxes? So we should make sure
        //  bounding boxes are tight

        long vol = object.getVoxelBox().extent().getVolume();
        int volXY = object.getVoxelBox().extent().getVolumeXY();
        int exY = object.getVoxelBox().extent().getX();

        while (true) {

            long index3D = randomNumberGenerator.sampleLongFromRange(vol);

            int slice = (int) (index3D / volXY);
            int index2D = (int) (index3D % volXY);

            byte b = object.getVoxelBox().getPixelsForPlane(slice).buffer().get(index2D);
            if (b == object.getBinaryValuesByte().getOnByte()) {

                int xRel = index2D % exY;
                int yRel = index2D / exY;
                int zRel = slice;

                int x = xRel + object.getBoundingBox().cornerMin().getX();
                int y = yRel + object.getBoundingBox().cornerMin().getY();
                int z = zRel + object.getBoundingBox().cornerMin().getZ();

                return new Point3d(x, y, z);
            }
        }
    }
}
