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
package org.anchoranalysis.test.image.rasterwriter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import org.anchoranalysis.image.extent.Extent;
import org.anchoranalysis.image.io.RasterIOException;
import org.anchoranalysis.image.io.bean.stack.StackWriter;
import org.anchoranalysis.image.io.stack.StackWriteOptions;
import org.anchoranalysis.image.stack.Stack;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.test.image.ChannelFixture;
import org.anchoranalysis.test.image.StackFixture;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StackTester {

    private static final String EXTENT_IDENTIFIER = "small";
    
    // START REQUIRED ARGUMENTS
    /**
     * The writer to use for creating new raster-files that are tested for bytewise equality against
     * saved rasters.
     */
    private final StackWriter writer;

    /** The directory to write new files to. */
    private final Path directoryToWriteTo;

    /** How to compare stacks by two different methods. */
    private final DualStackComparer comparer;
        
    /** If true, then 3D stacks are also tested and saved, not just 2D stacks. */
    private final boolean include3D;
    // END REQUIRED ARGUMENTS
        
    public void performTest(VoxelDataType[] channelVoxelTypes, int numberChannels, boolean makeRGB) throws RasterIOException, IOException {
        performTest(channelVoxelTypes, numberChannels, makeRGB, Optional.empty());
    }
    
    public void performTest(VoxelDataType[] channelVoxelTypes, int numberChannels, boolean makeRGB, Optional<VoxelDataType> forceFirstChannel)
            throws RasterIOException, IOException {
        for (VoxelDataType voxelType : channelVoxelTypes) {
            performTest(voxelType, numberChannels, makeRGB, forceFirstChannel);
        }
    }

    public void performTest(VoxelDataType channelVoxelType, int numberChannels, boolean makeRGB)
            throws RasterIOException, IOException {
        performTest(channelVoxelType, numberChannels, makeRGB, Optional.empty());
    }
    
    public void performTest(VoxelDataType channelVoxelType, int numberChannels, boolean makeRGB, Optional<VoxelDataType> forceFirstChannel)
            throws RasterIOException, IOException {
        test(channelVoxelType, numberChannels, makeRGB, ChannelFixture.SMALL_2D, false, forceFirstChannel);
        if (include3D) {
            test(channelVoxelType, numberChannels, makeRGB, ChannelFixture.SMALL_3D, true, forceFirstChannel);
        }
    }

    private void test(
            VoxelDataType channelVoxelType,
            int numberChannels,
            boolean makeRGB,
            Extent extent,
            boolean do3D,
            Optional<VoxelDataType> forceFirstChannel
    )
            throws RasterIOException, IOException {

        String filename =
                IdentifierHelper.identiferFor(
                        numberChannels, makeRGB, do3D, EXTENT_IDENTIFIER, channelVoxelType, forceFirstChannel.isPresent());

        Stack stack = new StackFixture(forceFirstChannel).create(numberChannels, extent, channelVoxelType);
        Path pathWritten =
                writer.writeStackWithExtension(
                        stack,
                        directoryToWriteTo.resolve(filename),
                        makeRGB,
                        StackWriteOptions.rgbMaybe3D());
        comparer.assertComparisons(pathWritten, filename);
    }
}
