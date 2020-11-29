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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.file.Path;
import lombok.AllArgsConstructor;
import org.anchoranalysis.core.progress.ProgressIgnore;
import org.anchoranalysis.image.core.stack.Stack;
import org.anchoranalysis.image.core.stack.TimeSequence;
import org.anchoranalysis.image.io.ImageIOException;
import org.anchoranalysis.image.io.bean.stack.reader.StackReader;
import org.anchoranalysis.image.io.stack.input.OpenedRaster;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
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
@AllArgsConstructor
class ExpectedImage {

    private static final String IMAGE_DIRECTORY = "images";

    /** The file extension of the iamge */
    private String extension;

    /** The filename (without extension) of the image somewhere in the images/ directory */
    private String fileNameWithoutExtension;

    /**
     * The expected count of voxels with intensity=={@code intensityValueToCount} in the first
     * channel.
     */
    private int expectedCount;

    /** The expected number of channels */
    private int expectedNumberChannels;

    /** The expected data-type of voxels */
    private VoxelDataType expectedDataType;

    /** Which intensity value to count */
    private int intensityValueToCount;

    public void openAndAssert(StackReader stackReader, TestLoader loader) throws ImageIOException {
        Stack stack = openStackFromReader(stackReader, loader);
        assertEqualsPrefix(
                "voxel data type", expectedDataType, stack.getChannel(0).getVoxelDataType());
        assertEqualsPrefix("number channels", expectedNumberChannels, stack.getNumberChannels());
        assertEqualsPrefix(
                "count of voxels==" + intensityValueToCount,
                expectedCount,
                stack.getChannel(0).voxelsEqualTo(intensityValueToCount).count());
    }

    private Stack openStackFromReader(StackReader reader, TestLoader loader)
            throws ImageIOException {

        Path path = loader.resolveTestPath(relativePath());

        OpenedRaster openedRaster = reader.openFile(path);
        TimeSequence timeSequence = openedRaster.open(0, ProgressIgnore.get());
        return timeSequence.get(0);
    }

    private void assertEqualsPrefix(String message, int expected, int actual) {
        assertEquals(fileNameWithoutExtension + " " + message, expected, actual);
    }

    private void assertEqualsPrefix(String message, Object expected, Object actual) {
        assertEquals(fileNameWithoutExtension + " " + message, expected, actual);
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
