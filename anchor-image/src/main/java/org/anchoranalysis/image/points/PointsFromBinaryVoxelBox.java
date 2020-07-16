/* (C)2020 */
package org.anchoranalysis.image.points;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.geometry.Point2i;
import org.anchoranalysis.core.geometry.Point3d;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.binary.values.BinaryValuesByte;
import org.anchoranalysis.image.binary.voxel.BinaryVoxelBox;
import org.anchoranalysis.image.extent.Extent;

public class PointsFromBinaryVoxelBox {

    private PointsFromBinaryVoxelBox() {}

    // Add: is added to each point before they are added to the list
    public static void addPointsFromVoxelBox(
            BinaryVoxelBox<ByteBuffer> voxelBox, ReadableTuple3i add, List<Point2i> listOut) {
        Extent e = voxelBox.extent();

        BinaryValuesByte bvb = voxelBox.getBinaryValues().createByte();
        ByteBuffer bb = voxelBox.getPixelsForPlane(0).buffer();

        for (int y = 0; y < e.getY(); y++) {
            for (int x = 0; x < e.getX(); x++) {

                if (bb.get() == bvb.getOnByte()) {

                    int xAdj = add.getX() + x;
                    int yAdj = add.getY() + y;

                    listOut.add(new Point2i(xAdj, yAdj));
                }
            }
        }
    }

    public static List<Point2i> pointsFromVoxelBox2D(BinaryVoxelBox<ByteBuffer> bvb)
            throws CreateException {

        List<Point2i> listOut = new ArrayList<>();

        if (bvb.extent().getZ() > 1) {
            throw new CreateException("Only works in 2D. No z-stack alllowed");
        }

        addPointsFromVoxelBox(bvb, new Point3i(0, 0, 0), listOut);

        return listOut;
    }

    // Add: is added to each point before they are added to the list
    public static void addPointsFromVoxelBox3D(
            BinaryVoxelBox<ByteBuffer> voxelBox, ReadableTuple3i add, Collection<Point3i> out) {

        Extent e = voxelBox.extent();

        BinaryValuesByte bvb = voxelBox.getBinaryValues().createByte();

        for (int z = 0; z < e.getZ(); z++) {
            ByteBuffer bb = voxelBox.getPixelsForPlane(z).buffer();

            int zAdj = add.getZ() + z;

            for (int y = 0; y < e.getY(); y++) {

                int yAdj = add.getY() + y;

                for (int x = 0; x < e.getX(); x++) {

                    if (bb.get() == bvb.getOnByte()) {

                        int xAdj = add.getX() + x;
                        out.add(new Point3i(xAdj, yAdj, zAdj));
                    }
                }
            }
        }
    }

    // Add: is added to each point before they are added to the list
    public static void addPointsFromVoxelBox3DDouble(
            BinaryVoxelBox<ByteBuffer> voxelBox, ReadableTuple3i add, Collection<Point3d> out) {

        Extent e = voxelBox.extent();

        BinaryValuesByte bvb = voxelBox.getBinaryValues().createByte();

        for (int z = 0; z < e.getZ(); z++) {
            ByteBuffer bb = voxelBox.getPixelsForPlane(z).buffer();

            int zAdj = add.getZ() + z;

            for (int y = 0; y < e.getY(); y++) {

                int yAdj = add.getY() + y;

                for (int x = 0; x < e.getX(); x++) {

                    if (bb.get() == bvb.getOnByte()) {

                        int xAdj = add.getX() + x;

                        out.add(new Point3d(xAdj, yAdj, zAdj));
                    }
                }
            }
        }
    }
}
