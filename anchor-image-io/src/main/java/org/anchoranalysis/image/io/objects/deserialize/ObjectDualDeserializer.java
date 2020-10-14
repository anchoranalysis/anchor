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

package org.anchoranalysis.image.io.objects.deserialize;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.core.serialize.DeserializationFailedException;
import org.anchoranalysis.core.serialize.Deserializer;
import org.anchoranalysis.core.serialize.ObjectInputStreamDeserializer;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.Dimensions;
import org.anchoranalysis.image.extent.box.BoundingBox;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.StackReader;
import org.anchoranalysis.image.io.stack.OpenedRaster;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;

/**
 * Deserializes an {@link ObjectMask} stored in two parts (a raster-mask and a serialized bounding-box)
 * <p>
 * Specifically, it expects:
 * <ol>
 * <li>A serialized {@link BoundingBox} <pre.somename.ser</pre>  (this filename is expected) in Java serialization form.
 * <li>An unsigned-8 bit raster-mask <pre>somename.tif</pre> corresponding to this bounding-box, using 255 as ON and 0 as on.
 * </ol>
 * @author Owen Feehan
 */
@AllArgsConstructor
class ObjectDualDeserializer implements Deserializer<ObjectMask> {

    private static final ObjectInputStreamDeserializer<BoundingBox> BOUNDING_BOX_DESERIALIZER =
            new ObjectInputStreamDeserializer<>();

    private final StackReader stackReader;

    @Override
    public ObjectMask deserialize(Path filePath) throws DeserializationFailedException {

        Path tiffFilename = changeExtension(filePath.toAbsolutePath(), "ser", "tif");

        BoundingBox box = BOUNDING_BOX_DESERIALIZER.deserialize(filePath);

        try (OpenedRaster or = stackReader.openFile(tiffFilename)) {
            Stack stack =
                    or.openCheckType(0, ProgressReporterNull.get(), UnsignedByteVoxelType.INSTANCE)
                            .get(0);

            if (stack.getNumberChannels() != 1) {
                throw new DeserializationFailedException("Raster file must have 1 channel exactly");
            }

            Channel channel = stack.getChannel(0);

            if (!channel.extent().equals(box.extent())) {
                throw new DeserializationFailedException(
                        errorMessageMismatchingDims(box, channel.dimensions(), filePath));
            }

            return new ObjectMask(box, channel.voxels().asByte());

        } catch (ImageIOException e) {
            throw new DeserializationFailedException(e);
        }
    }

    private static String errorMessageMismatchingDims(
            BoundingBox box, Dimensions dimensions, Path filePath) {
        return String.format(
                "Dimensions of bounding box (%s) and raster (%s) do not match for file %s",
                box.extent(), dimensions.extent(), filePath);
    }

    private static Path changeExtension(Path path, String oldExtension, String newExtension)
            throws DeserializationFailedException {

        String oldExtensionUpperCase = oldExtension.toUpperCase();

        if (!path.toString().endsWith("." + oldExtension)
                && !path.toString().endsWith("." + oldExtensionUpperCase)) {
            throw new DeserializationFailedException(
                    "Files must have ." + oldExtension + " extension");
        }

        // Change old extension into new extension
        return Paths.get(changeExtension(path.toString(), oldExtension, newExtension));
    }

    private static String changeExtension(String path, String oldExtension, String newExtension) {
        path = path.substring(0, path.length() - oldExtension.length());
        path = path.concat(newExtension);
        return path;
    }
}
