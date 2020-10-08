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
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.SignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.ByteFrom16BitUnsigned;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.ByteFrom32BitFloat;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.ByteFrom32BitUnsignedInt;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.ByteFrom8BitUnsignedInterleaving;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.ByteFrom8BitUnsignedNoInterleaving;
import org.anchoranalysis.io.bioformats.copyconvert.tobyte.ConvertToByte;
import org.anchoranalysis.io.bioformats.copyconvert.tofloat.ConvertToFloat;
import org.anchoranalysis.io.bioformats.copyconvert.tofloat.FloatFrom32Bit;
import org.anchoranalysis.io.bioformats.copyconvert.tofloat.FloatFrom8Bit;
import org.anchoranalysis.io.bioformats.copyconvert.toint.ConvertToInt;
import org.anchoranalysis.io.bioformats.copyconvert.toint.IntFromUnsigned32BitInt;
import org.anchoranalysis.io.bioformats.copyconvert.toshort.ConvertToShort;
import org.anchoranalysis.io.bioformats.copyconvert.toshort.ShortFromSignedShort;
import org.anchoranalysis.io.bioformats.copyconvert.toshort.ShortFromUnsignedShort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConvertToFactory {

    public static ConvertTo<?> create( // NOSONAR
            IFormatReader reader, VoxelDataType targetDataType, int effectiveBitsPerPixel)
            throws CreateException {

        boolean interleaved = reader.isInterleaved();
        boolean signed = FormatTools.isSigned(reader.getPixelType());
        int bitsPerPixel = maybeCorrectBitsPerPixel(reader.getBitsPerPixel());

        if (interleaved) {
            return createFromInterleaved(targetDataType, bitsPerPixel);
        } else {
            return createFromNonInterleaved(
                    reader, targetDataType, bitsPerPixel, effectiveBitsPerPixel, signed);
        }
    }

    private static ConvertTo<?> createFromInterleaved(
            VoxelDataType targetDataType, int bitsPerPixel) throws CreateException {
        if (targetDataType.equals(UnsignedByteVoxelType.INSTANCE) && bitsPerPixel == 8) {
            return new ByteFrom8BitUnsignedInterleaving();
        } else if (targetDataType.equals(UnsignedShortVoxelType.INSTANCE) && bitsPerPixel == 16) {
            return new ShortFromUnsignedShort(false);            
        } else {
            throw new CreateException("For interleaved formats only 8 and 16-bits are supported");
        }
    }

    private static ConvertTo<?> createFromNonInterleaved(
            IFormatReader reader,
            VoxelDataType targetDataType,
            int bitsPerPixel,
            int effectiveBitsPerPixel,
            boolean signed)
            throws CreateException {

        boolean littleEndian = reader.isLittleEndian();
        boolean floatingPoint = FormatTools.isFloatingPoint(reader.getPixelType());

        if (targetDataType.equals(UnsignedByteVoxelType.INSTANCE)) {
            return toByte(bitsPerPixel, effectiveBitsPerPixel, littleEndian, floatingPoint, signed);
        } else if (targetDataType.equals(UnsignedShortVoxelType.INSTANCE)
                || targetDataType.equals(SignedShortVoxelType.INSTANCE)) {
            return toShort(bitsPerPixel, littleEndian, signed);
        } else if (targetDataType.equals(FloatVoxelType.INSTANCE)) {
            return toFloat(bitsPerPixel, littleEndian, signed);
        } else if (targetDataType.equals(UnsignedIntVoxelType.INSTANCE)) {
            return toInt(bitsPerPixel, littleEndian, floatingPoint, signed);
        } else {
            throw new CreateException("Unsupported voxel data-type");
        }
    }

    private static ConvertToByte toByte(
            int bitsPerPixel,
            int effectiveBitsPerPixel,
            boolean littleEndian,
            boolean floatingPoint,
            boolean signed)
            throws CreateException {

        if (bitsPerPixel == 8 && !signed) {
            assert (effectiveBitsPerPixel == 8);
            return new ByteFrom8BitUnsignedNoInterleaving();

        } else if (bitsPerPixel == 16 && !signed) {
            return new ByteFrom16BitUnsigned(littleEndian, effectiveBitsPerPixel);

        } else if (bitsPerPixel == 32 && !signed) {

            if (floatingPoint) {
                return new ByteFrom32BitFloat(littleEndian);
            } else {
                return new ByteFrom32BitUnsignedInt(effectiveBitsPerPixel, littleEndian);
            }

        } else {
            return throwBitsPerPixelException(
                    "byte", "either unsigned 8 bits or 16 bits or 32 bits", bitsPerPixel);
        }
    }

    private static ConvertToShort toShort(int bitsPerPixel, boolean littleEndian, boolean signed)
            throws CreateException {

        if (bitsPerPixel == 16) {
            if (signed) {
                return new ShortFromSignedShort(littleEndian);
            } else {
                return new ShortFromUnsignedShort(littleEndian);
            }
        } else {
            return throwBitsPerPixelException("float", "16 bits", bitsPerPixel);
        }
    }

    private static ConvertToInt toInt(
            int bitsPerPixel, boolean littleEndian, boolean floatingPoint, boolean signed)
            throws CreateException {

        if (bitsPerPixel == 32 && !signed) {

            if (floatingPoint) {
                throw new CreateException(
                        "Conversion from floating-point to int not yet supported");
            } else {
                return new IntFromUnsigned32BitInt(littleEndian);
            }

        } else {
            return throwBitsPerPixelException("int", "unsigned 32 bits", bitsPerPixel);
        }
    }

    private static ConvertToFloat toFloat(int bitsPerPixel, boolean littleEndian, boolean signed)
            throws CreateException {
        assert (bitsPerPixel == 8 || bitsPerPixel == 32);

        if (bitsPerPixel == 8 && !signed) {
            return new FloatFrom8Bit();
        } else if (bitsPerPixel == 32 && signed) {
            return new FloatFrom32Bit(littleEndian);
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
                        "Input data for %s must have %s. It currently has %d bitsPerPixel.",
                        dataType, correctBitsDescription, bitsPerPixel));
    }

    private static int maybeCorrectBitsPerPixel(int bitsPerPixel) {
        if (bitsPerPixel == 12) {
            return 16;
        } else {
            return bitsPerPixel;
        }
    }
}
