package org.anchoranalysis.image.voxel.iterator.process;

/**
 * Processes two bytes.
 *
 * @author Owen Feehan
 */
@FunctionalInterface
public interface ProcessTwoBytes {
    void process(byte first, byte second);
}