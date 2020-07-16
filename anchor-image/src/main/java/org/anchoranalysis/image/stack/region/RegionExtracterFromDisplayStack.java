/* (C)2020 */
package org.anchoranalysis.image.stack.region;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.List;
import org.anchoranalysis.core.error.CreateException;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.core.geometry.ReadableTuple3i;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactory;
import org.anchoranalysis.image.extent.BoundingBox;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.scale.ScaleFactor;
import org.anchoranalysis.image.stack.DisplayStack;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.region.chnlconverter.attached.ChnlConverterAttached;
import org.anchoranalysis.image.voxel.box.VoxelBox;
import org.anchoranalysis.image.voxel.box.factory.VoxelBoxFactory;
import org.anchoranalysis.image.voxel.datatype.IncorrectVoxelDataTypeException;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedByte;
import org.anchoranalysis.image.voxel.datatype.VoxelDataTypeUnsignedShort;

public class RegionExtracterFromDisplayStack implements RegionExtracter {

    // Used to convert our source buffer to bytes, not called if it's already bytes
    private List<ChnlConverterAttached<Channel, ByteBuffer>> listChnlConverter;

    // Current displayStacl
    private Stack stack;

    public RegionExtracterFromDisplayStack(
            Stack stack, List<ChnlConverterAttached<Channel, ByteBuffer>> listChnlConverter) {
        super();
        this.stack = stack;
        this.listChnlConverter = listChnlConverter;
    }

    @Override
    public DisplayStack extractRegionFrom(BoundingBox bbox, double zoomFactor)
            throws OperationFailedException {

        Stack out = null;
        for (int c = 0; c < stack.getNumChnl(); c++) {

            Channel chnl =
                    extractRegionFrom(stack.getChnl(c), bbox, zoomFactor, listChnlConverter.get(c));

            if (c == 0) {
                out = new Stack(chnl);
            } else {
                try {
                    out.addChnl(chnl);
                } catch (IncorrectImageSizeException e) {
                    assert false;
                }
            }
        }
        try {
            return DisplayStack.create(out);
        } catch (CreateException e) {
            throw new OperationFailedException(e);
        }
    }

    // TODO put in some form of interpolation when zoomFactor<1
    private Channel extractRegionFrom(
            Channel extractedSlice,
            BoundingBox bbox,
            double zoomFactor,
            ChnlConverterAttached<Channel, ByteBuffer> chnlConverter)
            throws OperationFailedException {

        ScaleFactor sf = new ScaleFactor(zoomFactor);

        // We calculate how big our outgoing voxelbox wil be
        ImageDimensions sd = extractedSlice.getDimensions().scaleXYBy(sf);

        Extent extentTrgt = bbox.extent().scaleXYBy(sf);

        VoxelBox<ByteBuffer> bufferSc = VoxelBoxFactory.getByte().create(extentTrgt);

        MeanInterpolator interpolator = (zoomFactor < 1) ? new MeanInterpolator(zoomFactor) : null;

        if (extractedSlice.getVoxelDataType().equals(VoxelDataTypeUnsignedByte.INSTANCE)) {
            VoxelBox<ByteBuffer> vb = extractedSlice.getVoxelBox().asByte();
            interpolateRegionFromByte(
                    vb,
                    bufferSc,
                    extractedSlice.getDimensions().getExtent(),
                    extentTrgt,
                    bbox,
                    zoomFactor,
                    interpolator);

            if (chnlConverter != null) {
                chnlConverter.getVoxelBoxConverter().convertFromByte(bufferSc, bufferSc);
            }

        } else if (extractedSlice.getVoxelDataType().equals(VoxelDataTypeUnsignedShort.INSTANCE)
                && chnlConverter != null) {

            VoxelBox<ShortBuffer> vb = extractedSlice.getVoxelBox().asShort();

            VoxelBox<ShortBuffer> bufferIntermediate =
                    VoxelBoxFactory.getShort().create(extentTrgt);
            interpolateRegionFromShort(
                    vb,
                    bufferIntermediate,
                    extractedSlice.getDimensions().getExtent(),
                    extentTrgt,
                    bbox,
                    zoomFactor,
                    interpolator);

            // We now convert the ShortBuffer into bytes
            chnlConverter.getVoxelBoxConverter().convertFromShort(bufferIntermediate, bufferSc);

        } else {
            throw new IncorrectVoxelDataTypeException(
                    String.format(
                            "dataType %s is unsupported without chnlConverter",
                            extractedSlice.getVoxelDataType()));
        }

        return ChannelFactory.instance()
                .get(VoxelDataTypeUnsignedByte.INSTANCE)
                .create(bufferSc, sd.getRes());
    }

    // extentTrgt is the target-size (where we write this region)
    // extentSrcSlice is the source-size (the single slice we've extracted from the buffer to
    // interpolate from)
    private static void interpolateRegionFromByte(
            VoxelBox<ByteBuffer> vbSrc,
            VoxelBox<ByteBuffer> vbDest,
            Extent extentSrc,
            Extent extentTrgt,
            BoundingBox bbox,
            double zoomFactor,
            MeanInterpolator interpolator)
            throws OperationFailedException {

        ReadableTuple3i cornerMin = bbox.cornerMin();
        ReadableTuple3i cornerMax = bbox.calcCornerMax();
        for (int z = cornerMin.getZ(); z <= cornerMax.getZ(); z++) {

            ByteBuffer bbIn = vbSrc.getPixelsForPlane(z).buffer();
            ByteBuffer bbOut = vbDest.getPixelsForPlane(z - cornerMin.getZ()).buffer();

            // We go through every pixel in the new width, and height, and sample from the original
            // image
            int indOut = 0;
            for (int y = 0; y < extentTrgt.getY(); y++) {

                int yOrig = ((int) (y / zoomFactor)) + cornerMin.getY();
                for (int x = 0; x < extentTrgt.getX(); x++) {

                    int xOrig = ((int) (x / zoomFactor)) + cornerMin.getX();

                    // We get the byte to write
                    byte b =
                            (interpolator != null)
                                    ? interpolator.getInterpolatedPixelByte(
                                            xOrig, yOrig, bbIn, extentSrc)
                                    : bbIn.get(extentSrc.offset(xOrig, yOrig));

                    bbOut.put(indOut++, b);
                }
            }
        }
    }

    // extentTrgt is the target-size (where we write this region)
    // extentSrcSlice is the source-size (the single slice we've extracted from the buffer to
    // interpolate from)
    private static void interpolateRegionFromShort(
            VoxelBox<ShortBuffer> vbSrc,
            VoxelBox<ShortBuffer> vbDest,
            Extent extentSrc,
            Extent extentTrgt,
            BoundingBox bbox,
            double zoomFactor,
            MeanInterpolator interpolator)
            throws OperationFailedException {

        ReadableTuple3i cornerMin = bbox.cornerMin();
        ReadableTuple3i cornerMax = bbox.calcCornerMax();
        for (int z = cornerMin.getZ(); z <= cornerMax.getZ(); z++) {

            assert (vbSrc.getPixelsForPlane(z) != null);
            assert (vbDest.getPixelsForPlane(z - cornerMin.getZ()) != null);

            ShortBuffer bbIn = vbSrc.getPixelsForPlane(z).buffer();
            ShortBuffer bbOut = vbDest.getPixelsForPlane(z - cornerMin.getZ()).buffer();

            // We go through every pixel in the new width, and height, and sample from the original
            // image
            int indOut = 0;
            for (int y = 0; y < extentTrgt.getY(); y++) {

                int yOrig = ((int) (y / zoomFactor)) + cornerMin.getY();
                for (int x = 0; x < extentTrgt.getX(); x++) {

                    int xOrig = ((int) (x / zoomFactor)) + cornerMin.getX();

                    // We get the byte to write
                    short s =
                            (interpolator != null)
                                    ? interpolator.getInterpolatedPixelShort(
                                            xOrig, yOrig, bbIn, extentSrc)
                                    : bbIn.get(extentSrc.offset(xOrig, yOrig));

                    bbOut.put(indOut++, s);
                }
            }
        }
    }
}
