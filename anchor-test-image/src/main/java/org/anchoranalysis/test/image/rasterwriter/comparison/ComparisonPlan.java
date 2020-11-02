package org.anchoranalysis.test.image.rasterwriter.comparison;

import java.nio.file.Paths;
import java.util.Optional;
import org.anchoranalysis.core.functional.OptionalUtilities;
import org.anchoranalysis.test.image.DualComparer;
import org.anchoranalysis.test.image.rasterwriter.SavedFiles;
import org.junit.rules.TemporaryFolder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * A plan on which comparisons to execute for a test.
 * 
 * @author Owen Feehan
 *
 */
@Value @AllArgsConstructor(access=AccessLevel.PRIVATE)
public class ComparisonPlan {

    // START REQUIRED ARGUMENTS
    /**  */
    private final boolean bytewiseCompare;

    private final Optional<String> extensionVoxelwiseCompare;
    
    private final boolean skipComparisonForRGB; // NOSONAR
    
    private final Optional<String> directoryPathToCopyMissingImagesTo;
    // END REQUIRED ARGUMENTS
    
    /**
     * Creates a plan for particular settings.
     * 
     * @param bytewiseCompare iff true, a bytewise comparison occurs between the saved-file and the newly created file.
     * @param extensionVoxelwiseCompare iff defined, a voxel-wise comparison occurs with the saved-rasters from a different
     * extension.
     * @param skipComparisonForRGB if true, comparisons are not applied to RGB images.
     */
    public ComparisonPlan(boolean bytewiseCompare, Optional<String> extensionVoxelwiseCompare,
            boolean skipComparisonForRGB) {
        this(bytewiseCompare, extensionVoxelwiseCompare, skipComparisonForRGB, Optional.empty());
    }
    
    /**
     * Creates a plan for particular settings - and which copies any missing images to a particular directory.
     * 
     * <p>This helps create resource directories of saved-images, if they are missing.
     * 
     * @param bytewiseCompare iff true, a bytewise comparison occurs between the saved-file and the newly created file.
     * @param extensionVoxelwiseCompare iff defined, a voxel-wise comparison occurs with the saved-rasters from a different
     * extension.
     * @param skipComparisonForRGB if true, comparisons are not applied to RGB images.
     * @param directoryPathToCopyMissingImagesTo a path to a directory to copy images to if a test fails (which we assume is evidence the image is missing).
     */
    public ComparisonPlan(boolean bytewiseCompare, Optional<String> extensionVoxelwiseCompare,
            boolean skipComparisonForRGB, String directoryPathToCopyMissingImagesTo) {
        this(bytewiseCompare, extensionVoxelwiseCompare, skipComparisonForRGB, Optional.of(directoryPathToCopyMissingImagesTo));
    }
    
    /**
     * Creates the {@link ImageComparer} to use to compare between images loaded in a temporary-directory and saved-images.
     * 
     * @param directory the temporary-directory where an image is created to be compared
     * @param extension the file-extension used in the temporary-directory
     * @return a newly created comparer
     */
    public ImageComparer createComparer(TemporaryFolder directory, String extension) {
        return maybeCopyMissingImages( new CombineComparers(
                maybeCreateBytewiseComparer(directory, extension), maybeCreateVoxelwiseComparer(directory)));
    }
    
    private ImageComparer maybeCopyMissingImages(ImageComparer comparer) {
        if (directoryPathToCopyMissingImagesTo.isPresent()) {
            return new CopyMissingImages(comparer, Paths.get(directoryPathToCopyMissingImagesTo.get()));
        } else {
            return comparer;
        }
    }

    private Optional<ImageComparer> maybeCreateBytewiseComparer(TemporaryFolder directory, String extension) {
        return OptionalUtilities.createFromFlag(bytewiseCompare, () ->
           new CompareBytes(createComparer(extension, directory))
        );
    }

    private Optional<ImageComparer> maybeCreateVoxelwiseComparer(TemporaryFolder directory) {
        return extensionVoxelwiseCompare.map(
                extensionForComparer ->
                        new CompareVoxels(
                                    createComparer(extensionForComparer, directory), extensionForComparer));
    }
    
    private DualComparer createComparer(String extensionForComparer, TemporaryFolder directory) {
        return SavedFiles.createComparer(directory, extensionForComparer);
    }
}
