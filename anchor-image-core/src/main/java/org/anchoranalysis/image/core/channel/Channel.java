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

import com.google.common.base.Preconditions;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.anchoranalysis.core.exception.CreateException;
import org.anchoranalysis.core.exception.friendly.AnchorImpossibleSituationException;
import org.anchoranalysis.image.core.channel.factory.ChannelFactory;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.dimensions.IncorrectImageSizeException;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsUntyped;
import org.anchoranalysis.image.voxel.arithmetic.VoxelsArithmetic;
import org.anchoranalysis.image.voxel.assigner.VoxelsAssigner;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.image.voxel.extracter.VoxelsExtracter;
import org.anchoranalysis.image.voxel.extracter.predicate.VoxelsPredicate;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.image.voxel.resizer.VoxelsResizer;
import org.anchoranalysis.image.voxel.statistics.HistogramFactory;
import org.anchoranalysis.spatial.box.BoundingBox;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.spatial.point.Point3d;
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
     * Creates a {@link ObjectMask} representing all the voxels in {@code box} equal to {@code
     * equalValue}.
     *
     * <p>The equal values in the box are assigned an <i>on</i> state, and all other values an
     * <i>off</i> state.
     *
     * @param box the bounding-box to consider voxels within, and which forms the bounding-box for
     *     the created {@link ObjectMask}.
     * @param equalValue the value voxels must be equal to, so to be assigned an <i>on</i> state in
     *     the {@link ObjectMask}.
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
    public VoxelsUntyped voxels() {
        return new VoxelsUntyped(voxels);
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
     * Creates a new {@link Channel} containing only one particular slice.
     *
     * <p>The existing {@link Voxels} are reused, without creating new buffers.
     *
     * @param sliceIndex the index of the slice to extract (index in z-dimension).
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
     * @param scaleFactor the scaling-factor to be applied to the sizes.
     * @param resizer an interpolator for resizing voxels.
     * @return a newly created {@link Channel} containing a resized version of the current.
     */
    public Channel scaleXY(ScaleFactor scaleFactor, VoxelsResizer resizer) {
        // Rounding as sometimes we get values which, for example, are 7.999999, intended to be 8,
        // due to how we use our ScaleFactors
        int newSizeX = Scaler.scaleQuantity(scaleFactor.x(), dimensions().x());
        int newSizeY = Scaler.scaleQuantity(scaleFactor.y(), dimensions().y());
        return resizeXY(newSizeX, newSizeY, resizer);
    }

    /**
     * Resizes the dimensions of the channel, interpolating the existing voxel values to match.
     *
     * <p>It is an <i>immutable</i> operation, and the existing object state remains unchanged.
     *
     * <p>The z-dimension remains unchanged.
     *
     * @param extentToAssign the new size to assign. The z-component is ignored.
     * @param resizer an interpolator for resizing voxels.
     * @return a newly created {@link Channel} containing a resized version of the current.
     */
    public Channel resizeXY(Extent extentToAssign, VoxelsResizer resizer) {
        return resizeXY(extentToAssign.x(), extentToAssign.y(), resizer);
    }

    /**
     * Like {@link #resizeXY(Extent, VoxelsResizer)} but specifies the size via {@code int}
     * parameters.
     *
     * @param x the size along the x-axis.
     * @param y the size along the y-axis.
     * @param resizer an interpolator for resizing voxels.
     * @return a newly created {@link Channel} containing a resized version of the current.
     */
    public Channel resizeXY(int x, int y, VoxelsResizer resizer) {

        assert (FACTORY != null);

        Dimensions dimensionsScaled = dimensions.resizeXY(x, y);

        Voxels<?> resized = voxels.extract().resizedXY(x, y, resizer);
        return FACTORY.create(resized, dimensionsScaled.resolution());
    }

    /**
     * A <a href="https://en.wikipedia.org/wiki/Maximum_intensity_projection">maximum-intensity
     * projection</a> across the z-slices.
     *
     * @return a newly created {@link Channel} with a newly created voxel-buffer.
     */
    public Channel projectMax() {
        return flattenZProjection(VoxelsExtracter::projectMax);
    }

    /**
     * A mean-intensity projection across the z-slices.
     *
     * @return a newly created {@link Channel} with a newly created voxel-buffer.
     */
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
     * @return a newly instantiated object to perform queries on voxels who fulfill the above
     *     condition.
     */
    public VoxelsPredicate voxelsEqualTo(int equalToValue) {
        return voxels.extract().voxelsEqualTo(equalToValue);
    }

    /**
     * Operations on whether particular voxels are greater than a threshold (but not equal to).
     *
     * @param threshold voxel-values greater than this threshold are included.
     * @return a newly instantiated object to perform queries on voxels who fulfill the above
     *     condition.
     */
    public VoxelsPredicate voxelsGreaterThan(int threshold) {
        return voxels.extract().voxelsGreaterThan(threshold);
    }

    /**
     * Assigns a new resolution.
     *
     * <p>This is a <i>mutable</i> operation that replaces existing state.
     *
     * @param resolution the resolution to assign.
     */
    public void assignResolution(Optional<Resolution> resolution) {
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
        return HistogramFactory.createFrom(voxels()).toString();
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
                    dimensions
                            .resolution()
                            .map(resolution -> flattenResolutionZ(resolution, previousZSize)));
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

    /**
     * Derives a new {@link Resolution} in the z-dimension, after flattening has occurred across the
     * scene.
     *
     * <p>The new resolution reflects the sum of physical z-size of the existing resolution, before
     * the flattening.
     *
     * @param resolution the unflattend resolution.
     * @param unflattenedZSize the number of voxels in the Z-dimension before flattening.
     */
    private static Resolution flattenResolutionZ(Resolution resolution, int unflattenedZSize) {
        Preconditions.checkArgument(unflattenedZSize > 0);
        try {
            return new Resolution(
                    new Point3d(resolution.x(), resolution.y(), resolution.z() * unflattenedZSize));
        } catch (CreateException e) {
            throw new AnchorImpossibleSituationException();
        }
    }
}
