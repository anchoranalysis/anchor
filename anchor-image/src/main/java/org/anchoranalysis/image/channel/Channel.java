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

package org.anchoranalysis.image.channel;

import java.nio.Buffer;
import java.util.function.Function;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.ImageResolution;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.histogram.HistogramFactory;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.interpolator.InterpolatorImgLib2Lanczos;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsPredicate;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmetic;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;

/**
 * A channel from an image
 *
 * <p>This is one of the key image-processing classes in Anchor. An image may have one channel
 * (grayscale) or several. Channels of identical size can be bundled together to form a {@link
 * Stack}
 *
 * <p>The channel has an underlying
 *
 * @author Owen Feehan
 */
@Accessors(fluent = true)
public class Channel {

    private static final Interpolator DEFAULT_INTERPOLATOR = new InterpolatorImgLib2Lanczos();

    private static final ChannelFactory FACTORY = ChannelFactory.instance();

    @Getter private ImageDimensions dimensions;

    private Voxels<? extends Buffer> voxels;

    /**
     * Constructor
     *
     * @param voxels
     * @param resolution
     */
    public Channel(Voxels<? extends Buffer> voxels, ImageResolution resolution) {
        this.dimensions = new ImageDimensions(voxels.extent(), resolution);
        this.voxels = voxels;
    }

    public ObjectMask equalMask(BoundingBox box, int equalVal) {
        return voxels.extract().voxelsEqualTo(equalVal).deriveObject(box);
    }

    public VoxelsWrapper voxels() {
        return new VoxelsWrapper(voxels);
    }

    /**
     * Assigns new voxels to replace the existing voxels
     *
     * @param voxelsToAssign voxels-to-assign
     * @throws IncorrectImageSizeException
     */
    public void replaceVoxels(Voxels<?> voxelsToAssign) throws IncorrectImageSizeException {

        if (!voxelsToAssign.extent().equals(dimensions.extent())) {
            throw new IncorrectImageSizeException(
                    String.format(
                            "voxels to be assigned (%s) are not the same size as the existing voxels (%s)",
                            voxelsToAssign.extent(), dimensions.extent()));
        }

        this.voxels = voxelsToAssign;
    }

    /** Creates a new channel contain only of a particular slice (reusing the voxel buffers) */
    public Channel extractSlice(int z) {
        return ChannelFactory.instance()
                .create(voxels.extract().slice(z), dimensions.resolution());
    }

    public Channel scaleXY(ScaleFactor scaleFactor) {
        return scaleXY(scaleFactor, DEFAULT_INTERPOLATOR);
    }

    public Channel scaleXY(ScaleFactor scaleFactor, Interpolator interpolator) {
        // We round as sometimes we get values which, for example, are 7.999999, intended to be 8,
        // due to how we use our ScaleFactors
        int newSizeX = (int) Math.round(scaleFactor.x() * dimensions().x());
        int newSizeY = (int) Math.round(scaleFactor.y() * dimensions().y());
        return resizeXY(newSizeX, newSizeY, interpolator);
    }

    public Channel resizeXY(int x, int y) {
        return resizeXY(x, y, DEFAULT_INTERPOLATOR);
    }

    public Channel resizeXY(int x, int y, Interpolator interpolator) {

        assert (FACTORY != null);

        ImageDimensions dimensionsScaled = dimensions.scaleXYTo(x, y);

        Voxels<? extends Buffer> ba = voxels.extract().resizedXY(x, y, interpolator);
        assert (ba.extent().volumeXY() == ba.sliceBuffer(0).capacity());
        return FACTORY.create(ba, dimensionsScaled.resolution());
    }

    public Channel projectMax() {
        return flattenZProjection(VoxelsExtracter::projectMax);
    }

    public Channel projectMean() {
        return flattenZProjection(VoxelsExtracter::projectMean);
    }

    // Duplicates the current channel
    public Channel duplicate() {
        Channel dup = FACTORY.create(voxels.duplicate(), dimensions().resolution());
        assert (dup.voxels.extent().equals(voxels.extent()));
        return dup;
    }

    /**
     * Operations on whether particular voxels are equal to a particular value
     *
     * @param equalToValue
     * @return a newly instantiated object to perform queries to this voxels object as described
     *     above
     */
    public VoxelsPredicate voxelsEqualTo(int equalToValue) {
        return voxels.extract().voxelsEqualTo(equalToValue);
    }

    /**
     * Operations on whether particular voxels are greater than a treshold (but not equal to)
     *
     * @param threshold voxel-values greater than this threshold are included
     * @return a newly instantiated object to perform queries to this voxels object as described
     *     above
     */
    public VoxelsPredicate voxelsGreaterThan(int threshold) {
        return voxels.extract().voxelsGreaterThan(threshold);
    }

    public void updateResolution(ImageResolution res) {
        dimensions = dimensions.duplicateChangeRes(res);
    }

    public VoxelDataType getVoxelDataType() {
        return voxels.dataType();
    }

    /** Display a histogram of voxel-intensities as the string-representation */
    @Override
    public String toString() {
        try {
            return HistogramFactory.create(this).toString();
        } catch (CreateException e) {
            return String.format("Error: %s", e);
        }
    }

    public boolean equalsDeep(Channel other) {
        return dimensions.equals(other.dimensions) && voxels.equalsDeep(other.voxels);
    }

    /**
     * Flattens the voxels in the z direction, only if necessary (i.e. there's more than 1 z
     * dimension).
     *
     * @param voxels voxels to be flattened (i.e. 3D)
     * @param flattener function to perform the flattening
     * @return flattened box (i.e. 2D)
     */
    private Channel flattenZProjection(Function<VoxelsExtracter<?>, Voxels<?>> flattener) {
        int prevZSize = voxels.extent().z();
        if (prevZSize > 1) {
            return FACTORY.create(
                    flattener.apply(voxels.extract()),
                    dimensions.resolution().duplicateFlattenZ(prevZSize));
        } else {
            return this;
        }
    }

    public VoxelsArithmetic arithmetic() {
        return voxels.arithmetic();
    }

    public VoxelsAssigner assignValue(int valueToAssign) {
        return voxels.assignValue(valueToAssign);
    }

    public VoxelsExtracter<? extends Buffer> extract() { // NOSONAR
        return voxels.extract();
    }
    
    public Extent extent() {
        return voxels.extent();
    }
}
