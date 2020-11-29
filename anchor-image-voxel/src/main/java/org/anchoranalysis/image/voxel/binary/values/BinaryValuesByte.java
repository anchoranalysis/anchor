/*-
 * #%L
 * anchor-image
 * %%
 * Copyright (C) 2010 - 2020 Owen Feehan, ETH Zurich, University of Zurich, Hoffmann-La Roche
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

package org.anchoranalysis.image.voxel.binary.values;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.anchoranalysis.image.voxel.buffer.primitive.PrimitiveConverter;

/**
 * Two values representing {@code byte} binary states in an unsigned-byte buffer e.g. {@code 0} for
 * <i>OFF</i> and {@code -1} for <i>ON</i>
 *
 * <p>This class is <i>immutable</i>.
 *
 * <p>See {@link BinaryValues} for an equivalent class that stores these states as {@code int}
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
                PrimitiveConverter.unsignedByteToInt(offByte),
                PrimitiveConverter.unsignedByteToInt(onByte));
    }
}
