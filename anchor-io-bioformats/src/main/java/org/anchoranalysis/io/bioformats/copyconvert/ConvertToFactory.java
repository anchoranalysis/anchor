/*-
 * #%L
 * anchor-io-bioformats
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

package org.anchoranalysis.io.bioformats.copyconvert;

import loci.formats.FormatTools;
import loci.formats.IFormatReader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.SignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.ToUnsignedByte;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.UnsignedByteFromFloat;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.UnsignedByteFromUnsignedByteInterleaving;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.UnsignedByteFromUnsignedByteNoInterleaving;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.UnsignedByteFromUnsignedByteNoInterleavingScale;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.UnsignedByteFromUnsignedInt;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.UnsignedByteFromUnsignedShort;
import org.anchoranalysis.io.bioformats.copyconvert.tofloat.FloatFromUnsignedByte;
import org.anchoranalysis.io.bioformats.copyconvert.tofloat.FloatFromUnsignedInt;
import org.anchoranalysis.io.bioformats.copyconvert.tofloat.ToFloat;
import org.anchoranalysis.io.bioformats.copyconvert.toint.ToUnsignedInt;
import org.anchoranalysis.io.bioformats.copyconvert.toint.UnsignedIntFromUnsignedInt;
import org.anchoranalysis.io.bioformats.copyconvert.toshort.ToUnsignedShort;
import org.anchoranalysis.io.bioformats.copyconvert.toshort.UnsignedShortFromSignedShort;
import org.anchoranalysis.io.bioformats.copyconvert.toshort.UnsignedShortFromUnsignedShort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvertToFactory {

    public static ConvertTo<?> create( // NOSONAR
            IFormatReader reader, VoxelDataType targetDataType, int effectiveBitsPerPixel)
            throws CreateException {

        boolean interleaved = reader.isInterleaved();
        boolean signed = FormatTools.isSigned(reader.getPixelType());
        int bitsPerPixel = reader.getBitsPerPixel();

        if (interleaved) {
            return createFromInterleaved(targetDataType, bitsPerPixel);
        } else {
            return createFromNonInterleaved(
                    reader, targetDataType, bitsPerPixel, effectiveBitsPerPixel, signed);
        }
    }

    private static ConvertTo<?> createFromInterleaved(
            VoxelDataType targetDataType, int bitsPerPixel) throws CreateException {

        if (targetDataType.equals(UnsignedByteVoxelType.INSTANCE) && bitsPerPixel <= 8) {
            return new UnsignedByteFromUnsignedByteInterleaving();
        } else if (targetDataType.equals(UnsignedShortVoxelType.INSTANCE) && bitsPerPixel <= 16) {
            return new UnsignedShortFromUnsignedShort();
        } else {
            throw new CreateException(
                    "For interleaved formats only 1 <= bitDepth <= 16is supported");
        }
    }

    private static ConvertTo<?> createFromNonInterleaved(
            IFormatReader reader,
            VoxelDataType targetDataType,
            int bitsPerPixel,
            int effectiveBitsPerPixel,
            boolean signed)
            throws CreateException {

        boolean floatingPoint = FormatTools.isFloatingPoint(reader.getPixelType());

        if (targetDataType.equals(UnsignedByteVoxelType.INSTANCE)) {
            return toByte(bitsPerPixel, effectiveBitsPerPixel, floatingPoint, signed);
        } else if (targetDataType.equals(UnsignedShortVoxelType.INSTANCE)
                || targetDataType.equals(SignedShortVoxelType.INSTANCE)) {
            return toShort(bitsPerPixel, signed);
        } else if (targetDataType.equals(FloatVoxelType.INSTANCE)) {
            return toFloat(bitsPerPixel, signed);
        } else if (targetDataType.equals(UnsignedIntVoxelType.INSTANCE)) {
            return toInt(bitsPerPixel, floatingPoint, signed);
        } else {
            throw new CreateException("Unsupported voxel data-type");
        }
    }

    private static ToUnsignedByte toByte(
            int bitsPerPixel, int effectiveBitsPerPixel, boolean floatingPoint, boolean signed)
            throws CreateException {

        if (signed) {
            throw new CreateException(
                    "Only unsigned input data is currently supported to open as unsigned-byte");
        } else if (bitsPerPixel <= 7) {
            return new UnsignedByteFromUnsignedByteNoInterleavingScale(effectiveBitsPerPixel);
        } else if (bitsPerPixel <= 8) {
            return new UnsignedByteFromUnsignedByteNoInterleaving();

        } else if (bitsPerPixel <= 16) {
            return new UnsignedByteFromUnsignedShort(effectiveBitsPerPixel);

        } else if (bitsPerPixel <= 32) {

            if (floatingPoint) {
                return new UnsignedByteFromFloat();
            } else {
                return new UnsignedByteFromUnsignedInt(effectiveBitsPerPixel);
            }

        } else {
            return throwBitsPerPixelException(
                    "byte",
                    "unsigned and either in the range 1-8 bits or exactly 16 bits or exactly 32 bits",
                    bitsPerPixel);
        }
    }

    private static ToUnsignedShort toShort(int bitsPerPixel, boolean signed)
            throws CreateException {

        if (signed && bitsPerPixel == 16) {
            return new UnsignedShortFromSignedShort();
        } else if (!signed && bitsPerPixel >= 9 && bitsPerPixel <= 16) {
            // If one bitPerPixel more than what's accepted by a byte, then we assume it's encoded
            // as a short.
            return new UnsignedShortFromUnsignedShort();
        } else {
            return throwBitsPerPixelException("float", "16 bits", bitsPerPixel);
        }
    }

    private static ToUnsignedInt toInt(int bitsPerPixel, boolean floatingPoint, boolean signed)
            throws CreateException {

        // If one bitPerPixel more than what's accepted by a short, then we assume it's encoded as a
        // int.
        if (bitsPerPixel >= 17 && bitsPerPixel <= 32 && !signed) {

            if (floatingPoint) {
                throw new CreateException(
                        "Conversion from floating-point to int not yet supported");
            } else {
                return new UnsignedIntFromUnsignedInt();
            }

        } else {
            return throwBitsPerPixelException("int", "unsigned 32 bits", bitsPerPixel);
        }
    }

    private static ToFloat toFloat(int bitsPerPixel, boolean signed) throws CreateException {
        assert (bitsPerPixel == 8 || bitsPerPixel == 32);

        if (!signed && bitsPerPixel == 8) {
            return new FloatFromUnsignedByte();
        } else if (signed && bitsPerPixel == 32) {
            return new FloatFromUnsignedInt();
        } else {
            return throwBitsPerPixelException(
                    "float", "either unsigned 8 bits or signed 32 bits", bitsPerPixel);
        }
    }

    private static <T> T throwBitsPerPixelException(
            String dataType, String correctBitsDescription, int bitsPerPixel)
            throws CreateException {
        throw new CreateException(
                String.format(
                        "Input data to open as %s must have %s. It currently has %d bitsPerPixel.",
                        dataType, correctBitsDescription, bitsPerPixel));
    }
}
