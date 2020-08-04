/*-
 * #%L
 * anchor-image-io
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

package org.anchoranalysis.image.io.generator.raster.obj;

import java.nio.ByteBuffer;
import java.util.Optional;
import org.anchoranalysis.core.geometry.Point3i;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.generator.raster.RasterGenerator;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.buffer.VoxelBuffer;
import org.anchoranalysis.io.generator.IterableGenerator;
import org.anchoranalysis.io.manifest.ManifestDescription;
import org.anchoranalysis.io.output.error.OutputWriteFailedException;
import lombok.AllArgsConstructor;

/**
 * Outputs a channel but with ONLY the pixels in an object-mask shown, and others set to 0.
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public class ChnlMaskedWithObjectGenerator extends RasterGenerator
        implements IterableGenerator<ObjectMask> {

    private ObjectMask objectMask = null;
    private Channel srcChnl;


    public ChnlMaskedWithObjectGenerator(Channel srcChnl) {
        this.srcChnl = srcChnl;
    }

    @Override
    public Stack generate() throws OutputWriteFailedException {

        if (getIterableElement() == null) {
            throw new OutputWriteFailedException("no mutable element set");
        }

        Channel outChnl = createMaskedChnl(getIterableElement(), (Channel) srcChnl);
        return new Stack(outChnl);
    }

    @Override
    public ObjectMask getIterableElement() {
        return objectMask;
    }

    @Override
    public void setIterableElement(ObjectMask element) {
        objectMask = element;
    }

    @Override
    public RasterGenerator getGenerator() {
        return this;
    }

    @Override
    public Optional<ManifestDescription> createManifestDescription() {
        return Optional.of(new ManifestDescription("raster", "maskChnl"));
    }

    @Override
    public boolean isRGB() {
        return false;
    }

    /**
     * Creates a new channel, which copies voxels from srcChnl, but sets voxels outside the object-mask to
     * zero.
     *
     * <p>i.e. the new channel is a masked version of srcChnl
     *
     * @param object object-mask that determines the region that is copied (voxels outside this region are set to zero)
     * @param srcChnl the channel to copy
     * @return the masked channel
     */
    private static Channel createMaskedChnl(ObjectMask object, Channel srcChnl) {

        BoundingBox bbox = object.boundingBox();

        ImageDimensions newSd =
                new ImageDimensions(bbox.extent(), srcChnl.dimensions().resolution());

        Channel chnlNew =
                ChannelFactory.instance().createEmptyInitialised(newSd, srcChnl.getVoxelDataType());

        byte maskOn = object.binaryValuesByte().getOnByte();

        ReadableTuple3i maxGlobal = bbox.calcCornerMax();
        Point3i pointGlobal = new Point3i();
        Point3i pointLocal = new Point3i();

        Voxels<?> voxelsSrc = srcChnl.voxels().any();
        Voxels<?> voxelsNew = chnlNew.voxels().any();

        pointLocal.setZ(0);
        for (pointGlobal.setZ(bbox.cornerMin().z());
                pointGlobal.z() <= maxGlobal.z();
                pointGlobal.incrementZ(), pointLocal.incrementZ()) {

            ByteBuffer maskIn = object.voxels().slice(pointLocal.z()).buffer();
            VoxelBuffer<?> pixelsIn = voxelsSrc.slice(pointGlobal.z());
            VoxelBuffer<?> pixelsOut = voxelsNew.slice(pointLocal.z());

            for (pointGlobal.setY(bbox.cornerMin().y());
                    pointGlobal.y() <= maxGlobal.y();
                    pointGlobal.incrementY()) {

                for (pointGlobal.setX(bbox.cornerMin().x());
                        pointGlobal.x() <= maxGlobal.x();
                        pointGlobal.incrementX()) {

                    if (maskIn.get() != maskOn) {
                        continue;
                    }

                    int indexGlobal =
                            srcChnl.dimensions().offset(pointGlobal.x(), pointGlobal.y());
                    pixelsOut.putInt(maskIn.position() - 1, pixelsIn.getInt(indexGlobal));
                }
            }
        }

        return chnlNew;
    }
}
