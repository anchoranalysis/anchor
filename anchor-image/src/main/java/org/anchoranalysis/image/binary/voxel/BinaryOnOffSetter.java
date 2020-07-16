/* (C)2020 */
package org.anchoranalysis.image.binary.voxel;

import org.anchoranalysis.core.geometry.Point3i;

public interface BinaryOnOffSetter {

    boolean isOn(int x, int y, int z);

    boolean isOff(int x, int y, int z);

    void setOn(int x, int y, int z);

    void setOff(int x, int y, int z);

    default void setOn(Point3i point) {
        setOn(point.getX(), point.getY(), point.getZ());
    }

    default void setOff(Point3i point) {
        setOff(point.getX(), point.getY(), point.getZ());
    }

    default boolean isOn(Point3i point) {
        return isOn(point.getX(), point.getY(), point.getZ());
    }

    default boolean isOff(Point3i point) {
        return isOff(point.getX(), point.getY(), point.getZ());
    }
}
