/* (C)2020 */
package org.anchoranalysis.image.points;

import java.util.ArrayList;
import java.util.List;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.image.binary.mask.Mask;
import org.anchoranalysis.image.extent.BoundingBox;

public class PointsFromBinaryChnl {

    private PointsFromBinaryChnl() {}

    public static List<Point3i> pointsFromChnl(Mask chnl) {

        List<Point3i> listOut = new ArrayList<>();

        PointsFromBinaryVoxelBox.addPointsFromVoxelBox3D(
                chnl.binaryVoxelBox(), new Point3i(0, 0, 0), listOut);

        return listOut;
    }

    public static List<Point2i> pointsFromChnl2D(Mask chnl) throws CreateException {
        return PointsFromBinaryVoxelBox.pointsFromVoxelBox2D(chnl.binaryVoxelBox());
    }

    public static List<Point3i> pointsFromChnlInsideBox(
            Mask chnl, BoundingBox bbox, int startZ, int skipAfterSuccessiveEmptySlices) {

        List<Point3i> listOut = new ArrayList<>();

        PointsFromChnlHelper helper =
                new PointsFromChnlHelper(
                        skipAfterSuccessiveEmptySlices,
                        bbox.cornerMin(),
                        bbox.calcCornerMax(),
                        chnl.getChannel().getVoxelBox().asByte(),
                        chnl.getBinaryValues().createByte(),
                        startZ,
                        listOut);

        helper.firstHalf();

        // Exit early if we start on the first slice
        if (startZ == 0) {
            return listOut;
        }

        helper.secondHalf();

        return listOut;
    }
}
