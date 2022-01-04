/*-
 * #%L
 * anchor-test-image
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
package org.anchoranalysis.test.image.stackwriter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.writer.StackWriter;
import org.anchoranalysis.image.io.stack.output.StackWriteAttributesFactory;
import org.anchoranalysis.image.io.stack.output.StackWriteOptions;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.spatial.box.Extent;
import org.anchoranalysis.test.image.ChannelFixture;
import org.anchoranalysis.test.image.StackFixture;
import org.anchoranalysis.test.image.rasterwriter.comparison.ImageComparer;

@AllArgsConstructor
public class StackTester {

    public static final String EXTENT_IDENTIFIER = "small";

    /** A minimum file-size for all written rasters, below which we assume an error has occurred. */
    private static final int MINIMUM_FILE_SIZE = 20;

    // START REQUIRED ARGUMENTS
    /**
     * The writer to use for creating new raster-files that are tested for bytewise equality against
     * saved rasters.
     */
    private final StackWriter writer;

    /** The directory to write new files to. */
    private final Path directoryToWriteTo;

    /**
     * The file-extension to use for writing and testing files (case-sensitive, and without a
     * leading period).
     */
    private final String extension;

    /** If true, then 3D stacks are also tested and saved, not just 2D stacks. */
    private final boolean include3D;
    // END REQUIRED ARGUMENTS

    public void performTest(
            VoxelDataType[] channelVoxelTypes,
            int numberChannels,
            boolean makeRGB,
            Optional<ImageComparer> comparer)
            throws ImageIOException, IOException {
        performTest(channelVoxelTypes, numberChannels, makeRGB, Optional.empty(), comparer);
    }

    public void performTest(
            VoxelDataType[] channelVoxelTypes,
            int numberChannels,
            boolean makeRGB,
            Optional<VoxelDataType> forceFirstChannel,
            Optional<ImageComparer> comparer)
            throws ImageIOException, IOException {
        for (VoxelDataType voxelType : channelVoxelTypes) {
            performTest(
                    new ChannelSpecification(voxelType, numberChannels, makeRGB),
                    forceFirstChannel,
                    comparer);
        }
    }

    public void performTest(ChannelSpecification channels, Optional<ImageComparer> comparer)
            throws ImageIOException, IOException {
        performTest(channels, Optional.empty(), comparer);
    }

    public void performTest(
            ChannelSpecification channels,
            Optional<VoxelDataType> forceFirstChannel,
            Optional<ImageComparer> comparer)
            throws ImageIOException, IOException {
        test(channels, ChannelFixture.SMALL_2D, false, forceFirstChannel, comparer);
        if (include3D) {
            test(channels, ChannelFixture.SMALL_3D, true, forceFirstChannel, comparer);
        }
    }

    private void test(
            ChannelSpecification channels,
            Extent extent,
            boolean do3D,
            Optional<VoxelDataType> forceFirstChannel,
            Optional<ImageComparer> comparer)
            throws ImageIOException, IOException {

        String filename =
                IdentifierHelper.identiferFor(
                        channels, do3D, EXTENT_IDENTIFIER, forceFirstChannel.isPresent());

        Stack stack = new StackFixture(forceFirstChannel).create(channels, extent);

        StackWriteOptions options =
                new StackWriteOptions(
                        StackWriteAttributesFactory.maybeRGBWithoutAlpha(channels.isMakeRGB()),
                        Optional.empty());

        Path pathWritten =
                writer.writeStackWithExtension(
                        stack, directoryToWriteTo.resolve(filename), options);

        assertMinimumSize(pathWritten, filename);

        if (comparer.isPresent()) {
            comparer.get()
                    .assertIdentical(
                            filename,
                            ExtensionAdder.addExtension(filename, extension),
                            pathWritten);
        }
    }

    private void assertMinimumSize(Path path, String filenameWithoutExtension) throws IOException {
        assertTrue(
                Files.size(path) > MINIMUM_FILE_SIZE,
                () -> filenameWithoutExtension + "_minimumFileSize");
    }
}
