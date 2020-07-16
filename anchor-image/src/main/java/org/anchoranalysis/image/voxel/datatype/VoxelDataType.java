/* (C)2020 */
package org.anchoranalysis.image.voxel.datatype;

import org.apache.commons.lang.builder.HashCodeBuilder;

// The type of data contained within the channel
public abstract class VoxelDataType {

    private int numBits;
    private String typeIdentifier;
    private long maxValue;
    private long minValue;

    protected VoxelDataType(int numBits, String typeIdentifier, long maxValue, long minValue) {
        this.numBits = numBits;
        this.typeIdentifier = typeIdentifier;
        this.maxValue = maxValue;
        this.minValue = minValue;
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof VoxelDataType)) {
            return false;
        }

        VoxelDataType objC = (VoxelDataType) obj;

        if (isInteger() != objC.isInteger()) {
            return false;
        }

        if (isUnsigned() != objC.isUnsigned()) {
            return false;
        }

        return numBits() == objC.numBits();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(isInteger())
                .append(isUnsigned())
                .append(numBits())
                .toHashCode();
    }

    public abstract boolean isInteger();

    public abstract boolean isUnsigned();

    public final int numBits() {
        return numBits;
    }

    @Override
    public final String toString() {
        return typeIdentifier;
    }

    public final long maxValue() {
        return maxValue;
    }

    public final long minValue() {
        return minValue;
    }
}
