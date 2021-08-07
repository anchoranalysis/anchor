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

package org.anchoranalysis.image.voxel.interpolator;

import java.nio.FloatBuffer;
import lombok.RequiredArgsConstructor;
import net.imglib2.Cursor;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.AbstractRealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.ExtendedRandomAccessibleInterval;
import net.imglib2.view.Views;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedByteBuffer;
import org.anchoranalysis.image.voxel.buffer.primitive.UnsignedShortBuffer;
import org.anchoranalysis.image.voxel.convert.imglib2.ConvertToImg;
import org.anchoranalysis.spatial.Extent;

/**
 * Performs interpolation using ImgLib2
 *
 * <p>By default, voxels at the boundaries are mirrored (i.e. voxels just after the boundary are
 * treated like the closest voxel inside the boundary), but this can be changed to an extent
 * strategy instead.
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public abstract class InterpolatorImgLib2 implements Interpolator {

    /**
     * Added to the interpolated position as it is using the top-left interpolation scheme rather
     * than the center-scheme, where coordinates correspond to the 'center' point of a sample
     *
     * @see <a
     *     href="https://javadoc.scijava.org/ImgLib2/net/imglib2/interpolation/randomaccess/NearestNeighborInterpolator.html">ImgLib2
     *     interpolation Javadocs</a>
     */
    private static final double SHIFT_IN_INTERPOLATED_POSITION = -0.5;

    // START REQUIRED ARGUMENTS
    private final InterpolatorFactory<UnsignedByteType, RandomAccessible<UnsignedByteType>>
            factoryByte;

    private final InterpolatorFactory<UnsignedShortType, RandomAccessible<UnsignedShortType>>
            factoryShort;

    private final InterpolatorFactory<FloatType, RandomAccessible<FloatType>> factoryFloat;
    // END REQUIRED ARGUMENTS

    /** If set, rather than using the default mirroring strategy, an extend strategy is used */
    private boolean extend = false;

    /** If {@code extend==true} this value is used for all voxels outside the boundary */
    private int extendValue = 0;

    @Override
    public VoxelBuffer<UnsignedByteBuffer> interpolateByte(
            VoxelBuffer<UnsignedByteBuffer> voxelsSource,
            VoxelBuffer<UnsignedByteBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {

        Img<UnsignedByteType> in = ConvertToImg.fromByte(voxelsSource, extentSource);
        Img<UnsignedByteType> out = ConvertToImg.fromByte(voxelsDestination, extentDestination);

        RealRandomAccessible<UnsignedByteType> interpolant =
                Views.interpolate(outOfBoundsView(in), factoryByte);

        interpolate2D(interpolant, out, extentSource);

        return voxelsDestination;
    }

    @Override
    public VoxelBuffer<UnsignedShortBuffer> interpolateShort(
            VoxelBuffer<UnsignedShortBuffer> voxelsSource,
            VoxelBuffer<UnsignedShortBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {

        Img<UnsignedShortType> in = ConvertToImg.fromShort(voxelsSource, extentSource);
        Img<UnsignedShortType> out = ConvertToImg.fromShort(voxelsDestination, extentDestination);

        RealRandomAccessible<UnsignedShortType> interpolant =
                Views.interpolate(outOfBoundsView(in), factoryShort);

        interpolate2D(interpolant, out, extentSource);
        return voxelsDestination;
    }

    @Override
    public VoxelBuffer<FloatBuffer> interpolateFloat(
            VoxelBuffer<FloatBuffer> voxelsSource,
            VoxelBuffer<FloatBuffer> voxelsDestination,
            Extent extentSource,
            Extent extentDestination) {

        Img<FloatType> in = ConvertToImg.fromFloat(voxelsSource, extentSource);
        Img<FloatType> out = ConvertToImg.fromFloat(voxelsDestination, extentDestination);

        RealRandomAccessible<FloatType> interpolant =
                Views.interpolate(outOfBoundsView(in), factoryFloat);

        interpolate2D(interpolant, out, extentSource);
        return voxelsDestination;
    }

    /**
     * Switches to extend constant-value out-of-bounds strategy
     *
     * @param extendValue constant-value to use for all values outside the boundary
     */
    public void extendWith(int extendValue) {
        this.extend = true;
        this.extendValue = extendValue;
    }

    /** Multiplexes between two different strategies for handling out-of-bounds boundary values */
    private <T extends AbstractRealType<T>>
            ExtendedRandomAccessibleInterval<T, Img<T>> outOfBoundsView(Img<T> imgIn) {
        if (extend) {
            return Views.extendValue(imgIn, extendValue);
        } else {
            return Views.extendMirrorSingle(imgIn);
        }
    }

    private static <T extends Type<T>> Img<T> interpolate2D(
            RealRandomAccessible<T> source, Img<T> destination, Extent extentSrc) {

        // cursor to iterate over all pixels
        Cursor<T> cursor = destination.localizingCursor();

        // create a RealRandomAccess on the source (interpolator)
        RealRandomAccess<T> interpolatedAccess = source.realRandomAccess();

        double[] positionInterpolated = new double[2];

        double[] magnification = {
            destination.realMax(0) / extentSrc.x(), destination.realMax(1) / extentSrc.y()
        };

        // for all pixels of the output image
        while (cursor.hasNext()) {

            cursor.fwd();

            positionInterpolated[0] =
                    (cursor.getDoublePosition(0) / magnification[0])
                            + SHIFT_IN_INTERPOLATED_POSITION;
            positionInterpolated[1] =
                    (cursor.getDoublePosition(1) / magnification[1])
                            + SHIFT_IN_INTERPOLATED_POSITION;

            // set the position and extract the interpolated value
            interpolatedAccess.setPosition(positionInterpolated);
            cursor.get().set(interpolatedAccess.get());
        }

        return destination;
    }
}
