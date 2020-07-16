/* (C)2020 */
package org.anchoranalysis.image.binary.values;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.image.convert.ByteConverter;

/**
 * Two values representing {@code byte} binary states in an unsigned-byte buffer e.g. {@code 0} for
 * <i>OFF</i> and {@code -1} for <i>ON</i>
 *
 * <p>This class is <i>immutable</i>.
 *
 * <p>See {@link org.anchoranalysis.image.binary.values.BinaryValueBytes} for an equivalent class
 * that stores these states as {@code int}
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@EqualsAndHashCode
public final class BinaryValuesByte implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    private static final BinaryValuesByte DEFAULT = new BinaryValuesByte((byte) 0, (byte) -1);

    /** The byte representation of the value for OFF */
    @Getter private final byte offByte;

    /** The byte representation of the value for ON */
    @Getter private final byte onByte;

    public BinaryValuesByte(int off, int on) {
        this((byte) off, (byte) on);
    }

    public static BinaryValuesByte getDefault() {
        return DEFAULT;
    }

    public boolean isOn(byte val) {
        return val == onByte;
    }

    public boolean isOff(byte val) {
        return !isOn(val);
    }

    public BinaryValuesByte invert() {
        return new BinaryValuesByte(this.offByte, this.onByte);
    }

    public BinaryValues createInt() {
        return new BinaryValues(
                ByteConverter.unsignedByteToInt(offByte), ByteConverter.unsignedByteToInt(onByte));
    }
}
