/*-
 * #%L
 * anchor-plugin-io
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
package org.anchoranalysis.io.bioformats.bean.writer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.formats.FormatException;
import loci.formats.IFormatWriter;
import lombok.AllArgsConstructor;
import ome.xml.model.enums.PixelType;
import org.anchoranalysis.core.functional.FunctionalList;
import org.anchoranalysis.image.core.channel.Channel;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.writer.StackWriter;
import org.anchoranalysis.image.io.bean.stack.writer.WriterErrorMessageHelper;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;
import org.anchoranalysis.image.voxel.datatype.FindCommonVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;

/**
 * Base class for writing a stack to the filesystem using the <a
 * href="https://www.openmicroscopy.org/bio-formats/">Bioformats</a> library.
 *
 * <p>The following formats are supported with a variety of number of channels and stacks:
 *
 * <ul>
 *   <li>unsigned 8-bit
 *   <li>unsigned 16-bit
 *   <li>unsigned 32-bit
 *   <li>float
 * </ul>
 *
 * <p>Note that not all implementations support writing as RGB. When they do, it insists on three
 * channels, and only supported unsigned 8-bit or unsigned-16 bit as channel types.
 *
 * <p>If a stack has heterogeneous channel types (i.e. not all channels have the same type) then it
 * writes <i>all</i> channels with the most generic type (e.g. most bits).
 *
 * @author Owen Feehan
 */
@AllArgsConstructor
public abstract class BioformatsWriter extends StackWriter {

    /** Whether the writer supports writing RGB images or not. */
    private final boolean supportsRGB;

    @Override
    public void writeStack(Stack stack, Path filePath, StackWriteOptions options)
            throws ImageIOException {

        if (stack.getNumberChannels() == 0) {
            throw new ImageIOException("This stack has no channels to write.");
        }

        boolean rgb = options.getAttributes().writeAsRGB(stack);

        if (rgb && !supportsRGB) {
            throw new ImageIOException(
                    "Trying to write a RGB file but this writer does not support it.");
        }

        if (stack.allChannelsHaveIdenticalType()) {
            writeHomogeneousChannels(stack, filePath, rgb);
        } else {
            writeHeterogeneousChannels(stack, filePath, rgb);
        }
    }

    /**
     * Creates or gets an instance of {@link IFormatWriter} which dictates the file format to use
     * for writing.
     *
     * @return a newly created writer.
     * @throws ImageIOException if a writer cannot be created successfully.
     */
    protected abstract IFormatWriter createWriter() throws ImageIOException;

    /** When channels all have the same type. */
    private void writeHomogeneousChannels(Stack stack, Path filePath, boolean makeRGB)
            throws ImageIOException {
        VoxelDataType channelType = stack.getChannel(0).getVoxelDataType();

        VoxelTypeHelper.checkChannelTypeSupported(
                "Channels in stack the are an ",
                channelType,
                () -> writeStackInternal(stack, filePath, makeRGB, channelType));
    }

    /** When channels have varying types. */
    private void writeHeterogeneousChannels(Stack stack, Path filePath, boolean makeRGB)
            throws ImageIOException {
        // Find common type to represent all channels
        Stream<VoxelDataType> stream =
                stack.asListChannels().stream().map(Channel::getVoxelDataType);
        VoxelDataType commonType = FindCommonVoxelType.commonType(stream).get(); // NOSONAR

        VoxelTypeHelper.checkChannelTypeSupported(
                "The common channel representation for the channels in stack is ",
                commonType,
                () -> writeStackInternal(stack, filePath, makeRGB, commonType));
    }

    private void writeStackInternal(
            Stack stack, Path filePath, boolean makeRGB, VoxelDataType voxelDataTypeToWrite)
            throws ImageIOException {

        try (IFormatWriter writer = createWriter()) {
            prepareWriter(writer, stack, voxelDataTypeToWrite, makeRGB);

            writer.setId(filePath.toString());

            if (!writer.canDoStacks() && stack.dimensions().z() > 1) {
                throw new ImageIOException("The writer must support stacks for Z > 1");
            }
            writeStack(writer, stack, makeRGB, voxelDataTypeToWrite);

        } catch (IOException | FormatException e) {
            throw WriterErrorMessageHelper.generalWriteException(
                    BioformatsWriter.class, filePath, e);
        }
    }

    private static void writeStack(
            IFormatWriter writer, Stack stack, boolean makeRGB, VoxelDataType voxelDataTypeToWrite)
            throws ImageIOException {
        if (makeRGB) {
            writeRGB(writer, stack);
        } else {
            List<ByteRepresentationForChannel> channels =
                    FunctionalList.mapToList(
                            stack.asListChannels().stream(),
                            channel ->
                                    ByteRepresentationFactory.byteRepresentationFor(
                                            channel, voxelDataTypeToWrite));
            writeAsSeparateChannels(writer, channels, stack.dimensions().z());
        }
    }

    private static void writeRGB(IFormatWriter writer, Stack stack) throws ImageIOException {
        if (stack.getNumberChannels() != 3 && stack.getNumberChannels() != 4) {
            throw new ImageIOException(
                    "If makeRGB==true, then a stack must have exactly 3 or 4 channels, but it actually has: "
                            + stack.getNumberChannels());
        } else {
            boolean withAlpha = stack.getNumberChannels() == 4;

            if (stack.allChannelsHaveType(UnsignedByteVoxelType.INSTANCE)) {
                new RGBWriterByte(writer, stack, withAlpha).writeAsRGB();
            } else if (stack.allChannelsHaveType(UnsignedShortVoxelType.INSTANCE)) {
                new RGBWriterShort(writer, stack, withAlpha).writeAsRGB();
            } else {
                throw new ImageIOException(
                        "If makeRGB==true, then only unsigned 8-bit or unsigned 16-bit voxels are supported");
            }
        }
    }

    private static void prepareWriter(
            IFormatWriter writer, Stack stack, VoxelDataType voxelDataTypeToWrite, boolean makeRGB)
            throws ImageIOException {

        try {
            PixelType pixelType = VoxelTypeHelper.pixelTypeFor(voxelDataTypeToWrite);
            writer.setMetadataRetrieve(
                    MetadataUtilities.createMetadata(
                            stack.dimensions(),
                            stack.getNumberChannels(),
                            pixelType,
                            makeRGB,
                            false));
        } catch (ServiceException | DependencyException e) {
            throw new ImageIOException(
                    String.format(
                            "Failed to prepare the %s for writing an image.",
                            BioformatsWriter.class.getSimpleName()),
                    e);
        }
    }

    private static void writeAsSeparateChannels(
            IFormatWriter writer, List<ByteRepresentationForChannel> channels, int numberSlices)
            throws ImageIOException {
        int sliceIndex = 0;
        for (int channelIndex = 0; channelIndex < channels.size(); channelIndex++) {
            try {
                ByteRepresentationForChannel channel = channels.get(channelIndex);

                for (int z = 0; z < numberSlices; z++) {
                    writer.saveBytes(sliceIndex++, channel.bytesForSlice(z));
                }
            } catch (IOException | FormatException e) {
                throw new ImageIOException(
                        String.format(
                                "An error occurred when writing image channel (%d)", channelIndex),
                        e);
            }
        }
    }
}
