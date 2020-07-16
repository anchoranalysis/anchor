/* (C)2020 */
package org.anchoranalysis.image.voxel.iterator.changed;

import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.object.ObjectMask;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProcessVoxelNeighborFactory {

    /**
     * Within either a mask or an extent (as a fallback)
     *
     * @param containingMask if defined, the process is restricted to only process points within
     *     this mask
     * @param extentFallback if {@code containingMask} is not defined, then as a fallback, the
     *     process is restricted to only process points in this extent
     * @param process a process which will be wrapped inside a restriction
     * @return a new process with a restriction on the existing process
     */
    public static <T> ProcessVoxelNeighbor<T> within(
            Optional<ObjectMask> containingMask,
            Extent extentFallback,
            ProcessVoxelNeighborAbsolute<T> process) {
        return containingMask
                .map(mask -> withinMask(mask, process))
                .orElseGet(() -> withinExtent(extentFallback, process));
    }

    public static <T> ProcessVoxelNeighbor<T> withinExtent(
            ProcessVoxelNeighborAbsoluteWithSlidingBuffer<T> process) {
        return withinExtent(process.getExtent(), process);
    }

    public static <T> ProcessVoxelNeighbor<T> withinMask(
            ObjectMask object, ProcessChangedPointAbsoluteMasked<T> process) {
        return new WithinMask<>(process, object);
    }

    public static <T> ProcessVoxelNeighbor<T> withinMask(
            ObjectMask object, ProcessVoxelNeighborAbsolute<T> process) {
        return new WithinMask<>(new WrapAbsoluteAsMasked<>(process), object);
    }

    private static <T> ProcessVoxelNeighbor<T> withinExtent(
            Extent extent, ProcessVoxelNeighborAbsolute<T> process) {
        return new WithinExtent<>(extent, process);
    }
}
