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
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;

/**
 * Two values representing {@code byte} binary states in an {@link UnsignedByteBuffer}.
 *
 * <p>By default, these states are {@code 0} for <b>off</b> and {@code -1} (identical to {@code
 * 255}) for <b>on</b>.
 *
 * <p>This class is <i>immutable</i>.
 *
 * <p>See {@link BinaryValues} for an equivalent class that stores these states as {@code int}.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
@EqualsAndHashCode
public final class BinaryValuesByte implements Serializable {

    /** */
    private static final long serialVersionUID = 1L;

    private static final BinaryValuesByte DEFAULT = new BinaryValuesByte((byte) 0, (byte) -1);

    /** The byte representation of the value for <i>off</i>. */
    @Getter private final byte offByte;

    /** The byte representation of the value for <i>on</i>. */
    @Getter private final byte onByte;

    /**
     * Create with {@code int} values for <i>off</i> and <i>on</i> states.
     *
     * @param off value for the off state.
     * @param on value for the on state.
     */
    public BinaryValuesByte(int off, int on) {
        this((byte) off, (byte) on);
    }

    /**
     * Default values to use, if not otherwise specified.
     *
     * @return a static instance {@link BinaryValuesByte} with default values (see class
     *     description).
     */
    public static BinaryValuesByte getDefault() {
        return DEFAULT;
    }

    /**
     * Does a particular value correspond to the <i>on</i> state?
     *
     * @param value the value to check.
     * @return true iff it's identical to the <i>on</i> value.
     */
    public boolean isOn(byte value) {
        return value == onByte;
    }

    /**
     * Does a particular value correspond to the <i>off</i> state?
     *
     * @param value the value to check.
     * @return true iff it's identical to the <i>off</i> value.
     */
    public boolean isOff(byte value) {
        return !isOn(value);
    }

    /**
     * Inverts the values so <i>off</i> becomes <i>on</i>, and vice-versa.
     *
     * <p>This is an <i>immutable</i> operation.
     *
     * @return a {@link BinaryValuesByte} with the <i>off</i> and <i>on</i> values switched.
     */
    public BinaryValuesByte invert() {
        return new BinaryValuesByte(this.offByte, this.onByte);
    }

    /**
     * Derives a {@link BinaryValues} representation from the current values.
     *
     * <p>This is a similar structure but holds <i>unsigned int</i> values rather than <i>byte</i>
     * values.
     *
     * @return a newly derived {@link BinaryValuesByte}.
     */
    public BinaryValues asInt() {
        return new BinaryValues(
                PrimitiveConverter.unsignedByteToInt(offByte),
                PrimitiveConverter.unsignedByteToInt(onByte));
    }
}
