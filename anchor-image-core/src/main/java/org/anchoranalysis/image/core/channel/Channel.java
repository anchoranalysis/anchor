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

package org.anchoranalysis.image.core.channel;

import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.object.HistogramFromObjectsFactory;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmetic;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.image.voxel.extracter.predicate.VoxelsPredicate;
import org.anchoranalysis.image.voxel.interpolator.Interpolator;
import org.anchoranalysis.image.voxel.interpolator.InterpolatorImgLib2Lanczos;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.scale.ScaleFactor;
import org.anchoranalysis.spatial.scale.Scaler;

/**
 * A channel from an image.
 *
 * <p>This is one of the key image-processing classes in Anchor. An image may have one channel
 * (grayscale) or several. Channels of identical size can be bundled together to form a {@link
 * Stack}.
 *
 * <p>The channel's voxels have an underlying data-type that is not exposed as a templated
 * parameter, but can be accessed via {@link #getVoxelDataType}. This is deliberate weak-typing.
 *
 * <p>Each channel has an associated size and optionally defines a physical size for each voxel, as
 * stored in the associated {@link Dimensions} structure.
 *
 * @author Owen Feehan
 */
@Accessors(fluent = true)
public class Channel {

    /** The interpolator used when scaling/resizing unless another is explicitly specified. */
    private static final Interpolator DEFAULT_INTERPOLATOR = new InterpolatorImgLib2Lanczos();

    /** The factory to used to create any new {@link Channel}s. */
    private static final ChannelFactory FACTORY = ChannelFactory.instance();

    /** The size and voxel-resolution of the channel. */
    @Getter private Dimensions dimensions;

    /** The underlying voxels contained in the channel, with suppressed voxel type. */
    private Voxels<?> voxels;

    /**
     * Creates for particular voxels and resolution.
     *
     * @param voxels the voxels.
     * @param resolution the resolution, if it is known.
     */
    public Channel(Voxels<?> voxels, Optional<Resolution> resolution) {
        this.dimensions = new Dimensions(voxels.extent(), resolution);
        this.voxels = voxels;
    }

    /**
     * Creates a {@link ObjectMask} representing all the voxels in {@code box} equal to {@code equalValue}.
     * 
     * <p>The equal values in the box are assigned an <i>on</i> state, and all other values an <i>off</i> state.
     * 
     * @param box the bounding-box to consider voxels within, and which forms the bounding-box for the created {@link ObjectMask}.
     * @param equalValue the value voxels must be equal to, so to be assigned an <i>on</i> state in the {@link ObjectMask}.
     * @return a newly created {@link ObjectMask} as per above.
     */
    public ObjectMask equalMask(BoundingBox box, int equalValue) {
        return voxels.extract().voxelsEqualTo(equalValue).deriveObject(box);
    }

    /**
     * The underlying voxels in the channel.
     *
     * @return the voxels, wrapped in a class that facilitates friendly conversion.
     */
    public VoxelsWrapper voxels() {
        return new VoxelsWrapper(voxels);
    }

    /**
     * Assigns new voxels to replace the existing voxels.
     *
     * @param voxelsToAssign voxels to be assigned.
     * @throws IncorrectImageSizeException if {@code voxelsToAssign} has a different size to the
     *     dimensions of the channel.
     */
    public void replaceVoxels(Voxels<?> voxelsToAssign) throws IncorrectImageSizeException {

        if (voxelsToAssign.extent().equals(dimensions.extent())) {
            this.voxels = voxelsToAssign;
        } else {
            throw new IncorrectImageSizeException(
                    String.format(
                            "voxels to be assigned (%s) are not the same size as the existing voxels (%s)",
                            voxelsToAssign.extent(), dimensions.extent()));
        }
    }

    /**
     * Creates a new channel containing only one particular slice.
     *
     * <p>The existing {@link Voxels} are reused, without creating new buffers.
     *
     * @param sliceIndex the index of the slice to extract (index in z-dimension)
     * @return a newly created {@link Channel} consisting of the slice at {@code sliceIndex} only.
     */
    public Channel extractSlice(int sliceIndex) {
        return ChannelFactory.instance()
                .create(voxels.extract().slice(sliceIndex), dimensions.resolution());
    }

    /**
     * Resizes the dimensions of the channel, by scaling the existing size by a factor.
     * 
     * <p>Existing voxel values are interpolated to match the new size.
     * 
     * <p>This is useful for quickly downscaling or upscaling by a particular factor.
     * 
     * <p>It is an <i>immutable</i> operation, and the existing object state remains unchanged.
     * 
     * <p>The z-dimension remains unchanged.
     * 
     * @param scaleFactor the factor to multiply the existing X and Y size by.
     * @return a newly created {@link Channel} containing a resized version of the current.
     */
    public Channel scaleXY(ScaleFactor scaleFactor) {
        return scaleXY(scaleFactor, DEFAULT_INTERPOLATOR);
    }

    /**
     * Like {@link #scaleXY(ScaleFactor) but allows an explicit choice of {@link Interpolator}.
     * 
     * @param scaleFactor the scaling-factor to be applied to the sizes.
     * @param interpolator the interpolator.
     * @return a newly created {@link Channel} containing a resized version of the current.
     */
    public Channel scaleXY(ScaleFactor scaleFactor, Interpolator interpolator) {
        // Rounding as sometimes we get values which, for example, are 7.999999, intended to be 8,
        // due to how we use our ScaleFactors
        int newSizeX = Scaler.scaleQuantity(scaleFactor.x(), dimensions().x());
        int newSizeY = Scaler.scaleQuantity(scaleFactor.y(), dimensions().y());
        return resizeXY(newSizeX, newSizeY, interpolator);
    }

    /**
     * Resizes the dimensions of the channel, interpolating the existing voxel values to match.
     * 
     * <p>It is an <i>immutable</i> operation, and the existing object state remains unchanged.
     * 
     * <p>The z-dimension remains unchanged.
     * 
     * @param extentToAssign the new size to assign.
     * @return a newly created {@link Channel} containing a resized version of the current.
     */
    public Channel resizeXY(Extent extentToAssign) {
        return resizeXY(extentToAssign, DEFAULT_INTERPOLATOR);
    }

    /**
     * Like {@link #resizeXY(Extent)} but allows an explicit choice of {@link Interpolator}.
     * 
     * @param extentToAssign the new size to assign. The z-component is ignored.
     * @param interpolator the interpolator.
     * @return a newly created {@link Channel} containing a resized version of the current.
     */
    public Channel resizeXY(Extent extentToAssign, Interpolator interpolator) {
        return resizeXY(extentToAssign.x(), extentToAssign.y(), interpolator);
    }

    /**
     * Like {@link #resizeXY(Extent)} but specifies the size via {@code int} parameters.
     * 
     * @param x the size along the x-axis.
     * @param y the size along the y-axis.
     * @return a newly created {@link Channel} containing a resized version of the current.
     */
    public Channel resizeXY(int x, int y) {
        return resizeXY(x, y, DEFAULT_INTERPOLATOR);
    }

    /**
     * Like {@link #resizeXY(Extent, Interpolator)} but specifies the size via {@code int} parameters.
     * 
     * @param x the size along the x-axis.
     * @param y the size along the y-axis.
     * @param interpolator the interpolator.
     * @return a newly created {@link Channel} containing a resized version of the current.
     */
    public Channel resizeXY(int x, int y, Interpolator interpolator) {

        assert (FACTORY != null);

        Dimensions dimensionsScaled = dimensions.scaleXYTo(x, y);

        Voxels<?> resized = voxels.extract().resizedXY(x, y, interpolator);
        return FACTORY.create(resized, dimensionsScaled.resolution());
    }

    public Channel projectMax() {
        return flattenZProjection(VoxelsExtracter::projectMax);
    }

    public Channel projectMean() {
        return flattenZProjection(VoxelsExtracter::projectMean);
    }

    /**
     * A deep-copy.
     *
     * @return newly created deep-copy.
     */
    public Channel duplicate() {
        return FACTORY.create(voxels.duplicate(), dimensions().resolution());
    }

    /**
     * Operations on whether particular voxels are equal to a particular value.
     *
     * @param equalToValue the value all voxels should be equal to.
     * @return a newly instantiated object to perform queries to this voxels object as described
     *     above.
     */
    public VoxelsPredicate voxelsEqualTo(int equalToValue) {
        return voxels.extract().voxelsEqualTo(equalToValue);
    }

    /**
     * Operations on whether particular voxels are greater than a threshold (but not equal to).
     *
     * @param threshold voxel-values greater than this threshold are included.
     * @return a newly instantiated object to perform queries to this voxels object as described
     *     above.
     */
    public VoxelsPredicate voxelsGreaterThan(int threshold) {
        return voxels.extract().voxelsGreaterThan(threshold);
    }

    public void updateResolution(Optional<Resolution> resolution) {
        dimensions = dimensions.duplicateChangeResolution(resolution);
    }

    /**
     * The underlying data-type of the voxels in the channel, represented by a {@link VoxelDataType}
     * instance.
     *
     * @return an instance of {@link VoxelDataType}.
     */
    public VoxelDataType getVoxelDataType() {
        return voxels.dataType();
    }

    /** A string representation of a histogram of voxel-intensities in the channel. */
    @Override
    public String toString() {
        try {
            return HistogramFromObjectsFactory.create(this).toString();
        } catch (CreateException e) {
            return String.format("Error: %s", e);
        }
    }

    /**
     * Are the two channels equal using a deep voxel by voxel comparison?
     *
     * @param other the channel to compare with.
     * @param compareResolution if true, image-resolution must also be equal, otherwise it is not
     *     compared.
     * @return true if they are deemed equal, false otherwise.
     */
    public boolean equalsDeep(Channel other, boolean compareResolution) {
        return dimensions.equals(other.dimensions, compareResolution)
                && voxels.equalsDeep(other.voxels);
    }

    /**
     * Flattens the voxels in the z direction.
     *
     * <p>This only occurs if necessary (i.e. there's more than 1 z-slice), otherwise the current
     * object is returned unchanged.
     *
     * @param flattener function to perform the flattening.
     * @return a flattened channel, which is guaranteed to be 2D.
     */
    private Channel flattenZProjection(Function<VoxelsExtracter<?>, Voxels<?>> flattener) {
        int previousZSize = voxels.extent().z();
        if (previousZSize > 1) {
            return FACTORY.create(
                    flattener.apply(voxels.extract()),
                    dimensions.resolution().map(res -> res.duplicateFlattenZ(previousZSize)));
        } else {
            return this;
        }
    }

    /**
     * Interface that allows manipulation of voxel intensities via arithmetic operations.
     *
     * @return the interface.
     */
    public VoxelsArithmetic arithmetic() {
        return voxels.arithmetic();
    }

    /**
     * Interface that allows assignment of a particular value to all or subsets of the voxels.
     *
     * @param valueToAssign the value to assign.
     * @return the interface.
     */
    public VoxelsAssigner assignValue(int valueToAssign) {
        return voxels.assignValue(valueToAssign);
    }

    /**
     * Interface that allows read/copy/duplication operations to be performed regarding the voxels
     * intensities.
     *
     * @return the interface.
     */
    public VoxelsExtracter<?> extract() { // NOSONAR
        return voxels.extract();
    }

    /**
     * The size of the voxels across three dimensions.
     *
     * @return the size.
     */
    public Extent extent() {
        return voxels.extent();
    }

    /**
     * The resolution of the voxel that describes a physical size for each voxel.
     *
     * @return the resolution, if it exists.
     */
    public Optional<Resolution> resolution() {
        return dimensions.resolution();
    }
}
