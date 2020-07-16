/* (C)2020 */
package org.anchoranalysis.image.stack.bufferedimage;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import org.anchoranalysis.core.error.OperationFailedException;
import org.anchoranalysis.image.channel.Channel;
import org.anchoranalysis.image.channel.factory.ChannelFactoryByte;
import org.anchoranalysis.image.channel.factory.ChannelFactorySingleType;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.extent.ImageDimensions;
import org.anchoranalysis.image.extent.IncorrectImageSizeException;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.buffer.VoxelBufferByte;

public class CreateStackFromBufferedImage {

    private static final ChannelFactorySingleType FACTORY = new ChannelFactoryByte();

    private CreateStackFromBufferedImage() {}

    public static Stack create(BufferedImage bufferedImage) throws OperationFailedException {

        Stack stackOut = new Stack();

        ImageDimensions sd =
                new ImageDimensions(
                        new Extent(bufferedImage.getWidth(), bufferedImage.getHeight(), 1));

        byte[][] arr = bytesFromBufferedImage(bufferedImage);

        try {
            int numChnl = 3;
            for (int c = 0; c < numChnl; c++) {
                Channel chnl = FACTORY.createEmptyUninitialised(sd);
                chnl.getVoxelBox()
                        .asByte()
                        .getPlaneAccess()
                        .setPixelsForPlane(0, VoxelBufferByte.wrap(arr[c]));
                stackOut.addChnl(chnl);
            }

            return stackOut;
        } catch (IncorrectImageSizeException e) {
            throw new OperationFailedException(e);
        }
    }

    private static byte[][] bytesFromBufferedImage(BufferedImage image) {
        WritableRaster wr = image.getRaster();
        return bytesFromBufferedImage(wr, 0, 0, wr.getWidth(), wr.getHeight());
    }

    private static byte[][] bytesFromBufferedImage(
            WritableRaster wr, int x, int y, int sx, int sy) {

        if (x == 0
                && y == 0
                && sx == wr.getWidth()
                && sy == wr.getHeight()
                && wr.getDataBuffer() instanceof DataBufferByte) {
            return ((DataBufferByte) wr.getDataBuffer()).getBankData();
        }

        int bands = wr.getNumBands();
        byte[][] out = new byte[bands][sx * sy];
        int[] buf = new int[sx * sy];

        for (int i = 0; i < bands; i++) {
            wr.getSamples(x, y, sx, sy, i, buf);
            for (int j = 0; j < buf.length; j++) {
                out[i][j] = (byte) buf[j];
            }
        }
        return out;
    }
}
