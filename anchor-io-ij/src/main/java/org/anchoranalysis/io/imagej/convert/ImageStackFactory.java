package org.anchoranalysis.io.imagej.convert;

import java.nio.ByteBuffer;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.stack.rgb.RGBStack;
import org.anchoranalysis.image.voxel.Voxels;
import org.anchoranalysis.image.voxel.VoxelsWrapper;
import ij.ImageStack;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Routines for creating a {@link ImageStack}.
 * 
 * @author Owen Feehan
 *
 */
@NoArgsConstructor(access=AccessLevel.PRIVATE)
class ImageStackFactory {
    
    
    /**
     * Converts a {@link Stack} (as used in Anchor) into a {@link ImageStack} (for ImageJ).
     * 
     * @param stack the stack of channels to be converted into an {@link ImageStack} for ImageJ
     * @param makeRGB if true, the stack is assumed to have respectively red, green, blue channels) and outputted as a RGB-type image, otherwise an interleaved image-stack is created.
     * @return a newly created {@link ImageStack}
     */
    public static ImageStack createFromStack(Stack stack, boolean makeRGB) {
        if (makeRGB) {
            return createRGB(new RGBStack(stack));
        } else {
            return createInterleaved(stack);
        }
    }
    
    
    /**
     * Creates an ImageJ-RGB stack (i.e. using {@link ColorProcessor}.
     * 
     * @param stack an rgb-stack to convert
     * @return a newly created {@link ImageStack}
     **/
    public static ImageStack createRGB(RGBStack stack) {

        Extent extent = stack.channelAt(0).extent();

        int channelIndex = 0;

        Voxels<ByteBuffer> voxelsRed = extractChannel(stack, channelIndex++);
        Voxels<ByteBuffer> voxelsGreen = extractChannel(stack, channelIndex++);
        Voxels<ByteBuffer> voxelsBlue = extractChannel(stack, channelIndex);
        
        return createFromProcessorsStream(extent, z ->
            Stream.of( createColorProcessor(extent, z, voxelsRed, voxelsGreen, voxelsBlue) )
        );
    }
    
    /**
     * Create a {@link ImageStack} with interleaved channels from a {@link Stack}
     * 
     * @param stack the channels that will be interleaved
     * @return a newly created {@link ImageStack}
     */
    public static ImageStack createInterleaved(Stack stack) {
        return createFromVoxelsStream(stack.extent(), z -> 
            IntStream.range(0,stack.getNumberChannels()).mapToObj( index->
               stack.getChannel(index).voxels()
           )
        );
    }
    
    /**
     * Create an {@link ImageStack} composed entirely of a single channel
     * 
     * @param voxels the voxels corresponding to the single-channel
     * @return a newly created {@link ImageStack}
     */
    public static ImageStack createSingleChannel(VoxelsWrapper voxels) {
        return createFromVoxelsStream(voxels.extent(), z -> Stream.of(voxels) );
    }
    
    /**
     * Creates a new {@link ImageStack} of a certain size with a function that creates one or more {@link VoxelsWrapper} for each slice-index
     * 
     * @param extent the extent of the stack to create
     * @param createSlice creates one or more {@link VoxelsWrapper} to place into the {@link ImageStack} for a given slice-index
     * @return a newly created {@link ImageStack} with slices constructed from {@code createSlice} applied to all slice-indices
     */
    private static ImageStack createFromVoxelsStream(Extent extent, IntFunction<Stream<VoxelsWrapper>> createSlice) {
        return createFromProcessorsStream(extent, z -> createSlice.apply(z).map( voxels -> ConvertToImageProcessor.from(voxels, z)) );
    }

    /**
     * Creates a new {@link ImageStack} of a certain size with a function that creates one or more {@link ImageProcessor} for each slice-index
     * 
     * @param extent the extent of the stack to create
     * @param createSlice creates one or more {@link ImageProcessor} to place into the {@link ImageStack} for a given slice-index
     * @return a newly created {@link ImageStack} with slices constructed from {@code createSlice} applied to all slice-indices
     */
    private static ImageStack createFromProcessorsStream(Extent extent, IntFunction<Stream<ImageProcessor>> createSlice) {
        ImageStack stack = new ImageStack(extent.x(), extent.y());
        extent.iterateOverZ( z ->
            addSlices(stack, z, createSlice.apply(z))
        );
        return stack;
    }
    
    private static void addSlices(ImageStack stack, int z, Stream<ImageProcessor> slices) {
        slices.forEach( slice ->
            stack.addSlice(String.valueOf(z), slice)
        );
    }

    private static ColorProcessor createColorProcessor(Extent extent, int z, Voxels<ByteBuffer> voxelsRed, Voxels<ByteBuffer> voxelsGreen, Voxels<ByteBuffer> voxelsBlue) {
        ColorProcessor processor = new ColorProcessor(extent.x(), extent.y());

        byte[] redPixels = extractSlice(voxelsRed, z);
        byte[] greenPixels = extractSlice(voxelsGreen, z);
        byte[] bluePixels = extractSlice(voxelsBlue, z);

        processor.setRGB(redPixels, greenPixels, bluePixels);
        return processor;
    }
    
    private static Voxels<ByteBuffer> extractChannel(RGBStack stack, int channelIndex) {
        return stack.channelAt(channelIndex).voxels().asByte();
    }

    private static byte[] extractSlice(Voxels<ByteBuffer> voxels, int z) {
        return voxels.sliceBuffer(z).array();
    }
}
