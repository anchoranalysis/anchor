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
package org.anchoranalysis.test.image.rasterwriter.comparison;

import java.nio.file.Paths;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.anchoranalysis.core.format.ImageFileFormat;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.test.image.DualComparer;
import org.anchoranalysis.test.image.rasterwriter.SavedFiles;
import org.junit.rules.TemporaryFolder;

/**
 * A plan on which comparisons to execute for a test.
 *
 * @author Owen Feehan
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ComparisonPlan {

    // START REQUIRED ARGUMENTS
    /** */
    private final boolean bytewiseCompare;

    /** The file-format to use for the voxel-wise comparison (if it's activated). */
    private final Optional<ImageFileFormat> formatVoxelwiseCompare;

    private final boolean skipComparisonForRGB; // NOSONAR

    private final Optional<String> directoryPathToCopyMissingImagesTo;
    // END REQUIRED ARGUMENTS

    /**
     * Creates a plan for particular settings.
     *
     * @param bytewiseCompare iff true, a bytewise comparison occurs between the saved-file and the
     *     newly created file.
     * @param formatVoxelwiseCompare iff defined, a voxel-wise comparison occurs with the
     *     saved-rasters from a different format.
     * @param skipComparisonForRGB if true, comparisons are not applied to RGB images.
     */
    public ComparisonPlan(
            boolean bytewiseCompare,
            Optional<ImageFileFormat> formatVoxelwiseCompare,
            boolean skipComparisonForRGB) {
        this(bytewiseCompare, formatVoxelwiseCompare, skipComparisonForRGB, Optional.empty());
    }

    /**
     * Creates a plan for particular settings - and which copies any missing images to a particular
     * directory.
     *
     * <p>This helps create resource directories of saved-images, if they are missing.
     *
     * @param bytewiseCompare iff true, a bytewise comparison occurs between the saved-file and the
     *     newly created file.
     * @param formatVoxelwiseCompare iff defined, a voxel-wise comparison occurs with the
     *     saved-rasters from a different file-format.
     * @param skipComparisonForRGB if true, comparisons are not applied to RGB images.
     * @param directoryPathToCopyMissingImagesTo a path to a directory to copy images to if a test
     *     fails (which we assume is evidence the image is missing).
     */
    public ComparisonPlan(
            boolean bytewiseCompare,
            Optional<ImageFileFormat> formatVoxelwiseCompare,
            boolean skipComparisonForRGB,
            String directoryPathToCopyMissingImagesTo) {
        this(
                bytewiseCompare,
                formatVoxelwiseCompare,
                skipComparisonForRGB,
                Optional.of(directoryPathToCopyMissingImagesTo));
    }

    /**
     * Creates the {@link ImageComparer} to use to compare between images loaded in a
     * temporary-directory and saved-images.
     *
     * @param directory the temporary-directory where an image is created to be compared
     * @param extension the file-extension used in the temporary-directory
     * @return a newly created comparer
     */
    public ImageComparer createComparer(TemporaryFolder directory, String extension) {
        return maybeCopyMissingImages(
                new CombineComparers(
                        maybeCreateBytewiseComparer(directory, extension),
                        maybeCreateVoxelwiseComparer(directory)));
    }

    private ImageComparer maybeCopyMissingImages(ImageComparer comparer) {
        if (directoryPathToCopyMissingImagesTo.isPresent()) {
            return new CopyMissingImages(
                    comparer, Paths.get(directoryPathToCopyMissingImagesTo.get()));
        } else {
            return comparer;
        }
    }

    private Optional<ImageComparer> maybeCreateBytewiseComparer(
            TemporaryFolder directory, String extension) {
        return OptionalUtilities.createFromFlag(
                bytewiseCompare, () -> new CompareBytes(createComparer(extension, directory)));
    }

    private Optional<ImageComparer> maybeCreateVoxelwiseComparer(TemporaryFolder directory) {
        return formatVoxelwiseCompare
                .map(ImageFileFormat::getDefaultExtension)
                .map(
                        extension ->
                                new CompareVoxels(createComparer(extension, directory), extension));
    }

    private DualComparer createComparer(String extensionForComparer, TemporaryFolder directory) {
        return SavedFiles.createComparer(directory, extensionForComparer);
    }
}
