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

import lombok.RequiredArgsConstructor;
import org.anchoranalysis.image.io.bean.stack.writer.StackWriter;
import org.anchoranalysis.image.voxel.datatype.FloatVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedByteVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedIntVoxelType;
import org.anchoranalysis.image.voxel.datatype.UnsignedShortVoxelType;
import org.anchoranalysis.image.voxel.datatype.VoxelDataType;
import org.anchoranalysis.test.image.rasterwriter.comparison.ComparisonPlan;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

/**
 * Base class for testing various implementations of {@link StackWriter}.
 *
 * <p>The extension passed as a parameter determines where the particular directory saved-rasters
 * are saved to test against: {@code src/test/resources/stackWriter/formats/$EXTENSION}.
 *
 * <p>Two types of comparison are optionally possible:
 *
 * <ul>
 *   <li>Bytewise comparison, where the exact bytes on the file-system must be identical to the
 *       saved raster.
 *   <li>Voxelwise comparison, where the voxel intensity-values must be identical to the
 *       saved-raster.
 * </ul>
 *
 * @author Owen Feehan
 */
@RequiredArgsConstructor
public abstract class RasterWriterTestBase {

    /** All possible voxel types that can be supported. */
    protected static final VoxelDataType[] ALL_SUPPORTED_VOXEL_TYPES = {
        UnsignedByteVoxelType.INSTANCE,
        UnsignedShortVoxelType.INSTANCE,
        UnsignedIntVoxelType.INSTANCE,
        FloatVoxelType.INSTANCE
    };

    @Rule public TemporaryFolder directory = new TemporaryFolder();

    // START REQUIRED ARGUMENTS
    /** the extension (without a preceding period) to be tested and written. */
    private final String extension;

    /** If true, then 3D stacks are also tested and saved, not just 2D stacks. */
    private final boolean include3D;

    /** A plan on which comparisons to execute for a test. */
    private final ComparisonPlan comparisonPlan;
    // END REQUIRED ARGUMENTS

    /** Performs the tests. */
    protected FourChannelStackTester tester;

    @Before
    public void setup() {
        tester =
                new FourChannelStackTester(
                        new StackTester(
                                createWriter(), directory.getRoot().toPath(), extension, include3D),
                        comparisonPlan.createComparer(directory, extension),
                        comparisonPlan.isSkipComparisonForRGB());
    }

    /** Creates the {@link StackWriter} to be tested. */
    protected abstract StackWriter createWriter();
}
