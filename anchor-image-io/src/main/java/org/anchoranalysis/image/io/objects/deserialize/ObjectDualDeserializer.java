/* (C)2020 */
package org.anchoranalysis.image.io.objects.deserialize;

import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.progress.ProgressReporterNull;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.rasterreader.RasterReader;
import org.anchoranalysis.image.io.rasterreader.OpenedRaster;
import org.anchoranalysis.image.object.ObjectMask;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.io.bean.deserializer.Deserializer;
import org.anchoranalysis.io.bean.deserializer.ObjectInputStreamDeserializer;
import org.anchoranalysis.io.deserializer.DeserializationFailedException;

/**
 * Deserializes an {@link ObjectMask] stored in two parts (a raster-mask and a serialized bounding-box)
 * <p>
 * Specifically, it expects:
 * <ol>
 * <li>A serialized {@link BoundingBox} <pre.somename.ser</pre>  (this filename is expected) in Java serialization form.
 * <li>An unsigned-8 bit raster mask <pre>somename.tif</pre> corresponding to this bounding-box, using 255 as ON and 0 as on.
 * </ol>
 * @author Owen Feehan
 */
@AllArgsConstructor
class ObjectDualDeserializer implements Deserializer<ObjectMask> {

    private static final ObjectInputStreamDeserializer<BoundingBox> BOUNDING_BOX_DESERIALIZER =
            new ObjectInputStreamDeserializer<>();

    private final RasterReader rasterReader;

    @Override
    public ObjectMask deserialize(Path filePath) throws DeserializationFailedException {

        Path tiffFilename = changeExtension(filePath.toAbsolutePath(), "ser", "tif");

        BoundingBox bbox = BOUNDING_BOX_DESERIALIZER.deserialize(filePath);

        try (OpenedRaster or = rasterReader.openFile(tiffFilename)) {
            Stack stack =
                    or.openCheckType(
                                    0,
                                    ProgressReporterNull.get(),
                                    VoxelDataTypeUnsignedByte.INSTANCE)
                            .get(0);

            if (stack.getNumChnl() != 1) {
                throw new DeserializationFailedException("Raster file must have 1 channel exactly");
            }

            Channel chnl = stack.getChnl(0);

            if (!chnl.getDimensions().getExtent().equals(bbox.extent())) {
                throw new DeserializationFailedException(
                        errorMessageMismatchingDims(bbox, chnl.getDimensions(), filePath));
            }

            return new ObjectMask(bbox, chnl.getVoxelBox().asByte());

        } catch (RasterIOException e) {
            throw new DeserializationFailedException(e);
        }
    }

    private static String errorMessageMismatchingDims(
            BoundingBox bbox, ImageDimensions dimensions, Path filePath) {
        return String.format(
                "Dimensions of bounding box (%s) and raster (%s) do not match for file %s",
                bbox.extent(), dimensions.getExtent(), filePath);
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
