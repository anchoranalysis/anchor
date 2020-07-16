/* (C)2020 */
package org.anchoranalysis.image.binary.values;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;

/**
 * Two values representing {@link int} binary states in an unsigned-byte buffer e.g. 0 for OFF and 1
 * for ON
 *
 * <p>This class is <i>immutable</i>.
 *
 * <p>See {@link org.anchoranalysis.image.binary.values.BinaryValueBytes} for an equivalent class
 * that stores these states as {@link byte}
 *
 * @author Owen Feehan
 */
@EqualsAndHashCode
@AllArgsConstructor
public final class BinaryValues {

    /** Default values to use, if not otherwise specified */
    private static final BinaryValues DEFAULT = new BinaryValues(0, 255);

    /** The integer representation of the value for OFF */
    @Getter private final int offInt;

    /** The integer representation of the value for ON */
    @Getter private final int onInt;

    public BinaryValuesByte createByte() {
        if (offInt > 255) {
            throw new IncorrectVoxelDataTypeException("offInt must be <= 255");
        }
        if (onInt > 255) {
            throw new IncorrectVoxelDataTypeException("onInt must be <= 255");
        }
        return new BinaryValuesByte(offInt, onInt);
    }

    /** Default values to use, if not otherwise specified */
    public static BinaryValues getDefault() {
        return DEFAULT;
    }

    /** Inverts the values so OFF becomes ON, and vice-versa */
    public BinaryValues createInverted() {
        return new BinaryValues(onInt, offInt);
    }
}
