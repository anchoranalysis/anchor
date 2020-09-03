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
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.extent.Resolution;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.histogram.HistogramFactory;
import org.anchoranalysis.image.interpolator.Interpolator;
import org.anchoranalysis.image.interpolator.InterpolatorImgLib2Lanczos;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.scale.ScaleFactorUtilities;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsPredicate;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmetic;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;

/**
 * A channel from an image.
 *
 * <p>This is one of the key image-processing classes in Anchor. An image may have one channel
 * (grayscale) or several. Channels of identical size can be bundled together to form a {@link
 * Stack}.
 *
 * <p>The channel's voxels have an underlying data-type that is not exposed as a templated parameter, but
 * can be accessed via {@link #getVoxelDataType}.
 *
 * @author Owen Feehan
 */
@Accessors(fluent = true)
public class Channel {

    private static final Interpolator DEFAULT_INTERPOLATOR = new InterpolatorImgLib2Lanczos();

    private static final ChannelFactory FACTORY = ChannelFactory.instance();

    @Getter private Dimensions dimensions;

    private Voxels<? extends Buffer> voxels;

    /**
     * Constructor
     *
     * @param voxels
     * @param resolution
     */
    public Channel(Voxels<? extends Buffer> voxels, Resolution resolution) {
        this.dimensions = new Dimensions(voxels.extent(), resolution);
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
        return ChannelFactory.instance().create(voxels.extract().slice(z), dimensions.resolution());
    }

    public Channel scaleXY(ScaleFactor scaleFactor) {
        return scaleXY(scaleFactor, DEFAULT_INTERPOLATOR);
    }

    public Channel scaleXY(ScaleFactor scaleFactor, Interpolator interpolator) {
        // We round as sometimes we get values which, for example, are 7.999999, intended to be 8,
        // due to how we use our ScaleFactors
        int newSizeX = ScaleFactorUtilities.scaleQuantity(scaleFactor.x(), dimensions().x());
        int newSizeY = ScaleFactorUtilities.scaleQuantity(scaleFactor.y(), dimensions().y());
        return resizeXY(newSizeX, newSizeY, interpolator);
    }

    public Channel resizeXY(int x, int y) {
        return resizeXY(x, y, DEFAULT_INTERPOLATOR);
    }

    public Channel resizeXY(int x, int y, Interpolator interpolator) {

        assert (FACTORY != null);

        Dimensions dimensionsScaled = dimensions.scaleXYTo(x, y);

        Voxels<? extends Buffer> resized = voxels.extract().resizedXY(x, y, interpolator);
        assert (resized.extent().volumeXY() == resized.sliceBuffer(0).capacity());
        return FACTORY.create(resized, dimensionsScaled.resolution());
    }

    public Channel projectMax() {
        return flattenZProjection(VoxelsExtracter::projectMax);
    }

    public Channel projectMean() {
        return flattenZProjection(VoxelsExtracter::projectMean);
    }

    // Duplicates the current channel
    public Channel duplicate() {
        Channel duplicated = FACTORY.create(voxels.duplicate(), dimensions().resolution());
        assert (duplicated.voxels.extent().equals(voxels.extent()));
        return duplicated;
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

    public void updateResolution(Resolution resolution) {
        dimensions = dimensions.duplicateChangeResolution(resolution);
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

    /**
     * Are the two channels equal using a deep voxel by voxel comparison?
     * 
     * @param other the channel to compare with
     * @param compareResolution if true, image-resolution must also be equal, otherwise it is not compared.
     * @return true if they are deemed equal, false otherwise.
     */
    public boolean equalsDeep(Channel other, boolean compareResolution) {
        return dimensions.equals(other.dimensions, compareResolution) && voxels.equalsDeep(other.voxels);
    }

    /**
     * Flattens the voxels in the z direction, only if necessary (i.e. there's more than 1 z
     * dimension).
     *
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

    public Resolution resolution() {
        return dimensions.resolution();
    }
}
