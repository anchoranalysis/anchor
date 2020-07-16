/* (C)2020 */
package org.anchoranalysis.image.voxel;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.Descriptive;
import org.anchoranalysis.image.convert.ByteConverter;

/** A list of intensity values of voxels */
public final class VoxelIntensityList {

    private DoubleArrayList lst;

    public VoxelIntensityList() {
        lst = new DoubleArrayList();
    }

    public VoxelIntensityList(int initialCapacity) {
        lst = new DoubleArrayList(initialCapacity);
    }

    public final void add(double val) {
        lst.add(val);
    }

    public final void add(byte val) {
        lst.add(ByteConverter.unsignedByteToInt(val));
    }

    public final double sum() {
        return Descriptive.sum(lst);
    }

    public final double mean() {
        return Descriptive.mean(lst);
    }

    public final double variance(double mean) {
        int size = lst.size();
        double sum = mean * size;
        return Descriptive.variance(size, sum, sumOfSquares());
    }

    public final long sumOfSquares() {
        return (long) Descriptive.sumOfSquares(lst);
    }

    public final double standardDeviation(double variance) {

        return Descriptive.standardDeviation(variance);
    }

    public int size() {
        return lst.size();
    }

    public double get(int arg0) {
        return lst.get(arg0);
    }

    public void addAllOf(VoxelIntensityList arg0) {
        lst.addAllOf(arg0.lst);
    }
}
