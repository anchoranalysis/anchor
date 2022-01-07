/*-
 * #%L
 * anchor-io-bioformats
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
package org.anchoranalysis.io.bioformats.bean;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.anchoranalysis.core.log.Logger;
import org.anchoranalysis.core.time.ExecutionTimeRecorder;
import org.anchoranalysis.image.core.dimensions.Resolution;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.io.stack.input.OpenedImageFile;
import org.anchoranalysis.image.io.stack.time.TimeSequence;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.test.LoggerFixture;
import org.anchoranalysis.test.TestLoader;

/**
 * The location of an image and some expectations that should be asserted.
 *
 * <p>The expectations are:
 *
 * <ul>
 *   <li>the voxel-data-type
 *   <li>the number of channels
 *   <li>the count of intensity values equal to a particular value.
 * </ul>
 *
 * <p>The path of image should be, relative to the test-loader,
 * images/<b>${extension}</b>/<b>${fileNameWithoutExtension}</b>.<b>${extension}</b>
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
class ExpectedImage {

    private static final String IMAGE_DIRECTORY = "images";

    // START: REQUIRED ARGUMENTS
    /** The file extension of the iamge */
    private final String extension;

    /** The filename (without extension) of the image somewhere in the images/ directory */
    private final String fileNameWithoutExtension;

    /**
     * The expected count of voxels with intensity=={@code intensityValueToCount} in the first
     * channel.
     */
    private final int expectedCount;

    /** The expected number of channels */
    private final int expectedNumberChannels;

    /** The expected data-type of voxels */
    private final VoxelDataType expectedDataType;

    /** The expected image-resolution. */
    private final Optional<Resolution> expectedResolution;

    /** Which intensity value to count */
    private final int intensityValueToCount;
    // END: REQUIRED ARGUMENTS

    private Logger logger = LoggerFixture.suppressedLogger();

    public void openAndAssert(
            StackReader stackReader, TestLoader loader, ExecutionTimeRecorder executionTimeRecorder)
            throws ImageIOException {
        Stack stack = openStackFromReader(stackReader, loader, executionTimeRecorder);
        assertEqualsPrefix(
                "voxel data type", expectedDataType, stack.getChannel(0).getVoxelDataType());
        assertEqualsPrefix("number channels", expectedNumberChannels, stack.getNumberChannels());
        assertEqualsPrefix(
                "count of voxels==" + intensityValueToCount,
                expectedCount,
                stack.getChannel(0).voxelsEqualTo(intensityValueToCount).count());
        assertEquals(expectedResolution, stack.resolution());
    }

    private Stack openStackFromReader(
            StackReader reader, TestLoader loader, ExecutionTimeRecorder executionTimeRecorder)
            throws ImageIOException {

        Path path = loader.resolveTestPath(relativePath());

        OpenedImageFile openedFile = reader.openFile(path, executionTimeRecorder);
        TimeSequence timeSequence = openedFile.open(logger);
        return timeSequence.get(0);
    }

    private void assertEqualsPrefix(String message, int expected, int actual) {
        assertEquals(expected, actual, () -> fileNameWithoutExtension + " " + message);
    }

    private void assertEqualsPrefix(String message, Object expected, Object actual) {
        assertEquals(expected, actual, () -> fileNameWithoutExtension + " " + message);
    }

    private String relativePath() {
        StringBuilder builder = new StringBuilder();
        builder.append(IMAGE_DIRECTORY);
        builder.append(File.separator);
        builder.append(extension);
        builder.append(File.separator);
        builder.append(fileNameWithoutExtension);
        builder.append(".");
        builder.append(extension);
        return builder.toString();
    }
}
