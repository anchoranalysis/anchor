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

package org.anchoranalysis.image.io.object.input;

import java.nio.file.Path;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.exception.OperationFailedException;
import org.anchoranalysis.core.format.FormatExtensions;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.core.format.NonImageFileFormat;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.core.serialize.DeserializationFailedException;
import org.anchoranalysis.core.serialize.Deserializer;
import org.anchoranalysis.core.serialize.ObjectInputStreamDeserializer;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.dimensions.Dimensions;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.object.ObjectMask;
import org.anchoranalysis.spatial.box.BoundingBox;

/**
 * Deserializes an {@link ObjectMask} stored in two parts (a raster-mask and a serialized bounding-box)
 * <p>
 * Specifically, it expects:
 * <ol>
 * <li>A serialized {@link BoundingBox} <pre.somename.ser</pre>  (this filename is expected) in Java serialization form.
 * <li>An unsigned-8 bit raster-mask <pre>somename.tif</pre> corresponding to this bounding-box, using 255 as <i>on</i> and 0 as on.
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
        try {
            Path tiffFilename =
                    FormatExtensions.changeExtension(
                            filePath.toAbsolutePath(),
                            NonImageFileFormat.SERIALIZED_BINARY,
                            ImageFileFormat.TIFF);
            return createFromPaths(filePath, tiffFilename);
        } catch (OperationFailedException e) {
            throw new DeserializationFailedException(e);
        }
    }

    private ObjectMask createFromPaths(Path pathSerializedBox, Path pathTiff)
            throws DeserializationFailedException {
        BoundingBox box = BOUNDING_BOX_DESERIALIZER.deserialize(pathSerializedBox);

        try (OpenedImageFile openedFile = stackReader.openFile(pathTiff)) {
            Stack stack =
                    openedFile
                            .openCheckType(0, ProgressIgnore.get(), UnsignedByteVoxelType.INSTANCE)
                            .get(0);

            if (stack.getNumberChannels() != 1) {
                throw new DeserializationFailedException("Raster file must have 1 channel exactly");
            }

            Channel channel = stack.getChannel(0);

            if (!channel.extent().equals(box.extent())) {
                throw new DeserializationFailedException(
                        errorMessageMismatchingDims(box, channel.dimensions(), pathSerializedBox));
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
}
